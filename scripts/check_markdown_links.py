#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
LINK_PATTERN = re.compile(r"!?\[[^\]]*\]\(([^)]+)\)")
IGNORED_PREFIXES = ("http://", "https://", "mailto:", "#")


def markdown_files() -> list[Path]:
    ignored_parts = {".git", "build", ".gradle"}
    return [
        path
        for path in ROOT.rglob("*.md")
        if not any(part in ignored_parts for part in path.parts)
    ]


def normalize_target(raw_target: str) -> str:
    target = raw_target.strip()
    if target.startswith("<") and target.endswith(">"):
        target = target[1:-1]
    target = target.split("#", 1)[0].split("?", 1)[0]
    return unquote(target)


def main() -> int:
    failures: list[str] = []
    checked = 0

    for markdown in markdown_files():
        content = markdown.read_text(encoding="utf-8")
        for match in LINK_PATTERN.finditer(content):
            raw_target = match.group(1).strip()
            if not raw_target or raw_target.startswith(IGNORED_PREFIXES):
                continue

            target = normalize_target(raw_target)
            if not target:
                continue

            checked += 1
            candidate = (markdown.parent / target).resolve()
            try:
                candidate.relative_to(ROOT.resolve())
            except ValueError:
                failures.append(
                    f"{markdown.relative_to(ROOT)} links outside repository: {raw_target}",
                )
                continue

            if not candidate.exists():
                failures.append(
                    f"{markdown.relative_to(ROOT)} has missing link target: {raw_target}",
                )

    if failures:
        print("Internal Markdown link check failed:")
        for failure in failures:
            print(f"- {failure}")
        return 1

    print(f"Internal Markdown link check passed ({checked} relative links checked).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
