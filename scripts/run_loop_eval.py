#!/usr/bin/env python3
from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

GRADLE_TASK = ":integration-test:lincheck:loopEval"
OUTPUT_ROOT = Path("build/loop-eval/raw")


def parse_args() -> argparse.Namespace:
    p = argparse.ArgumentParser(description="Run Lincheck loop-evaluation suite")
    p.add_argument("mode_label", help="Label only: BASELINE / BOUNDED / ADAPTIVE")
    p.add_argument("--suite", choices=["core", "micro", "all"], default="core")
    p.add_argument("--repetitions", type=int, default=5)
    p.add_argument("--stress", action="store_true", help="Include stress runs")
    p.add_argument("--gradlew", default="./gradlew")
    return p.parse_args()


def run_loop_eval(gradlew: str, mode: str, suite: str, repetitions: int, run_stress: bool) -> int:
    OUTPUT_ROOT.mkdir(parents=True, exist_ok=True)
    args_flag = f"{mode} {repetitions} {str(run_stress).lower()}"
    cmd = [gradlew, GRADLE_TASK, f"-Dlincheck.loopEval.suite={suite}", "--args", args_flag, "--console=plain"]
    proc = subprocess.run(cmd)
    return proc.returncode


def expected_output(mode: str, suite: str) -> Path:
    return OUTPUT_ROOT / f"results_{mode.lower()}_{suite.lower()}.csv"


def main() -> int:
    args = parse_args()

    code = run_loop_eval(args.gradlew, args.mode_label, args.suite, args.repetitions, args.stress)
    if code != 0:
        return code

    output = expected_output(args.mode_label, args.suite)
    if output.exists():
        print(f"[loop-eval] wrote raw results to {output}")
    else:
        print(f"[loop-eval] expected output not found: {output}")
        return 2

    print("[loop-eval] NOTE: detector mode must already be selected manually in ManagedStrategy.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
