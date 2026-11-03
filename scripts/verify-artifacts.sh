#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <binary-jar> <javadoc-jar>" >&2
  exit 2
fi

binary_jar="$1"
javadoc_jar="$2"

test -f "$binary_jar"
test -f "$javadoc_jar"

binary_entries="$(jar tf "$binary_jar")"
javadoc_entries="$(jar tf "$javadoc_jar")"

if ! printf '%s\n' "$binary_entries" \
  | grep -Fx 'ro/apanescu/railml/generated/v32/RailML.class' >/dev/null; then
  echo "The binary JAR is missing the RailML 3.2 root model class." >&2
  exit 1
fi

if ! printf '%s\n' "$javadoc_entries" \
  | grep -Fx 'ro/apanescu/railml/parser/RailMLParser.html' >/dev/null; then
  echo "The Javadoc JAR is missing the documented parser API." >&2
  exit 1
fi

for jar_file in "$binary_jar" "$javadoc_jar"; do
  entries="$(jar tf "$jar_file")"
  if printf '%s\n' "$entries" \
    | grep -Ei '(^|/)[^/]+\.xsd$|(^|/)railML[^/]+/|(^|/)sample\.xml$' >/dev/null; then
    echo "The JAR unexpectedly contains excluded schema or sample files: $jar_file" >&2
    exit 1
  fi
done
