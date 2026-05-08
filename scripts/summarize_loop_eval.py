#!/usr/bin/env python3
from __future__ import annotations

import csv
from collections import defaultdict
from pathlib import Path
from statistics import median

RAW_ROOT = Path("build/loop-eval/raw")
OUT_ROOT = Path("build/loop-eval/summaries")


def load_rows() -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    for csv_path in RAW_ROOT.glob("*.csv"):
        with csv_path.open("r", encoding="utf-8") as f:
            rows.extend(csv.DictReader(f))
    return rows


def to_int(value: str) -> int:
    try:
        return int(value)
    except ValueError:
        return 0


def summarize(rows: list[dict[str, str]]) -> list[dict[str, str]]:
    grouped: dict[tuple[str, str], list[dict[str, str]]] = defaultdict(list)
    for row in rows:
        grouped[(row["benchmark"], row["mode"])].append(row)

    summary_rows: list[dict[str, str]] = []
    for (benchmark, mode), items in sorted(grouped.items()):
        runtimes = [to_int(x["runtime_ms"]) for x in items]
        explored = [to_int(x.get("explored_schedules", "0")) for x in items]
        switches = [to_int(x.get("switch_thread_count", "0")) for x in items]
        stuck = [to_int(x.get("stuck_count", "0")) for x in items]
        idle = [to_int(x.get("idle_count", "0")) for x in items]
        await = [to_int(x.get("await_count", "0")) for x in items]
        cas = [to_int(x.get("cas_count", "0")) for x in items]
        zne = [to_int(x.get("zne_count", "0")) for x in items]
        unknown = [to_int(x.get("unknown_count", "0")) for x in items]
        failures = sum(1 for x in items if to_int(x.get("failure_found", "0")) > 0)

        summary_rows.append({
            "benchmark": benchmark,
            "mode": mode,
            "runs": str(len(items)),
            "failures": str(failures),
            "mean_runtime_ms": f"{sum(runtimes) / len(runtimes):.2f}" if runtimes else "0",
            "median_runtime_ms": f"{median(runtimes):.2f}" if runtimes else "0",
            "mean_explored_schedules": f"{sum(explored) / len(explored):.2f}" if explored else "0",
            "mean_switches": f"{sum(switches) / len(switches):.2f}" if switches else "0",
            "mean_stuck": f"{sum(stuck) / len(stuck):.2f}" if stuck else "0",
            "mean_idle": f"{sum(idle) / len(idle):.2f}" if idle else "0",
            "mean_await": f"{sum(await) / len(await):.2f}" if await else "0",
            "mean_cas": f"{sum(cas) / len(cas):.2f}" if cas else "0",
            "mean_zne": f"{sum(zne) / len(zne):.2f}" if zne else "0",
            "mean_unknown": f"{sum(unknown) / len(unknown):.2f}" if unknown else "0",
        })
    return summary_rows


def write_csv(rows: list[dict[str, str]], path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(
            f,
            fieldnames=[
                "benchmark",
                "mode",
                "runs",
                "failures",
                "mean_runtime_ms",
                "median_runtime_ms",
                "mean_explored_schedules",
                "mean_switches",
                "mean_stuck",
                "mean_idle",
                "mean_await",
                "mean_cas",
                "mean_zne",
                "mean_unknown",
            ],
        )
        writer.writeheader()
        writer.writerows(rows)


def main() -> int:
    rows = load_rows()
    if not rows:
        print("No raw results found under build/loop-eval/raw")
        return 1

    summary_rows = summarize(rows)
    OUT_ROOT.mkdir(parents=True, exist_ok=True)
    write_csv(summary_rows, OUT_ROOT / "summary.csv")
    print(f"Wrote {OUT_ROOT / 'summary.csv'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
