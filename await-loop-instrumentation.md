
# Current Await Loop Instrumentation Flow

## Step 1: Detection — `computeAwaitLoops`

For each loop in the method, a **path-sensitive BFS** runs from the loop header through the loop body. It looks for at least one path from the header to a back-edge source that is:

- **Clean**: every block on the path has no shared writes (`PUTFIELD`, `PUTSTATIC`, `xASTORE`), no monitor ops, and no side-effecting calls (everything except `Thread.onSpinWait` counts as side-effecting).
- **Read-bearing**: at least one block on the path performs a shared read (`GETFIELD`, `GETSTATIC`, `xALOAD`).

If such a path exists, the loop is classified as an **await loop**, and the specific back-edge source blocks at the end of those clean paths are recorded.

The result is:

```
awaitLoopBackEdgeSources: Map<LoopId, Set<BasicBlockIndex>>
```

For each await loop, this maps the loop ID to the set of "clean" back-edge source blocks.

---

## Step 2: Injection Site Computation

Two things are derived from the detection result:

1. **`awaitLoopIds: Set<LoopId>`** — simply `awaitLoopBackEdgeSources.keys`. These loops are **excluded** from the normal `onLoopIteration` header instrumentation.

2. **`awaitSpinSites: Map<InstructionIndex, LoopId>`** — computed by `computeAwaitSpinSites`. For each clean back-edge source block, it takes the **last opcode** of that block (the `GOTO`/jump that loops back to the header) and maps its non-phony instruction index to the loop ID.

---

## Step 3: Bytecode Injection — `beforeInsn`

At each instruction visited, three things are checked:

### 3a. Loop header hit (`iterationEntrySites`)

If this instruction is a loop header **and** the loop is **not** an await loop → inject `onLoopIteration`.

If the loop **is** an await loop → **nothing is injected at the header** (it's skipped entirely).

### 3b. Await spin site hit (`awaitSpinSites`)

If this instruction is the last opcode of a clean back-edge source block for an await loop → inject `onAwaitLoopSpin`.

This fires **only** when execution reaches the end of a read-only spin-retry path, right before the `GOTO` jumps back to the header.

### 3c. Loop exit sites (`normalExitSites` / `exceptionExitSites`)

These inject `afterLoopExit` regardless of whether the loop is an await loop or not — both await and non-await loops get exit instrumentation.

---

## The Key Design Point

The instrumentation for await loops is **not** at the loop header. Instead it's at the **back-edge sources** — specifically only the "clean" ones. This means:

- Iterations that take a **dirty path** (e.g., the CAS branch, a write, a return) do **not** fire `onAwaitLoopSpin` — they either exit the loop (triggering `afterLoopExit`) or take a back-edge through a non-clean block (which has no injection).
- Only iterations that follow the **read-only spin-retry path** all the way to a clean back-edge fire `onAwaitLoopSpin`.

**However**, the tradeoff is that for an await loop, `onLoopIteration` is **never** called — not even on the iterations that do real work. The loop is **entirely** classified as either await or non-await; there's no per-iteration dual instrumentation. The reasoning is that you didn't want both `onLoopIteration` and `onAwaitLoopSpin` on the same loop — it's one or the other.

---

## Concrete Example: `removeFirstOrNull` (Example 3)

- The `_state.loop { }` is detected as an await loop because the `element == null → return@loop` path is clean (only reads `array[head].value`).
- **At the loop header**: no `onLoopIteration` is injected (it's an await loop).
- **At the `return@loop` back-edge** (the clean spin-retry path): `onAwaitLoopSpin` **is** injected.
- **At the CAS path or early returns**: those exit the loop, so `afterLoopExit` fires.
- **At the `!singleConsumer → return@loop` back-edge after CAS failure**: this path goes through the CAS (a side-effecting call), so it's a **dirty** back-edge — no `onAwaitLoopSpin` is injected there.
