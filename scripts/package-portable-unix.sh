#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_ROOT="$ROOT/dist"
APP_DIR="$DIST_ROOT/SiteLedger"
JAR_NAME="SiteLedger.jar"

cd "$ROOT"

if [ ! -x "./mvnw" ]; then
  chmod +x ./mvnw
fi

./mvnw clean package -DskipTests

JAR_PATH="$(find "$ROOT/target" -maxdepth 1 -name '*.jar' ! -name '*.original' -print0 | xargs -0 ls -t | head -n 1)"

if [ -z "$JAR_PATH" ]; then
  echo "No runnable jar found in target." >&2
  exit 1
fi

mkdir -p "$APP_DIR"
cp "$JAR_PATH" "$APP_DIR/$JAR_NAME"

if [ -f "$ROOT/employee-payroll.db" ]; then
  cp "$ROOT/employee-payroll.db" "$APP_DIR/employee-payroll.db"
fi

cat > "$APP_DIR/start-siteledger.command" <<'EOF'
#!/usr/bin/env bash
cd "$(dirname "$0")"

if ! command -v java >/dev/null 2>&1; then
  echo "Java was not found."
  echo "Install Java 17 or newer, then run this file again."
  echo "Download: https://adoptium.net/temurin/releases/?version=17"
  read -r -p "Press Enter to close..."
  exit 1
fi

open "http://127.0.0.1:8081" >/dev/null 2>&1 || true
java -jar SiteLedger.jar
read -r -p "Press Enter to close..."
EOF

chmod +x "$APP_DIR/start-siteledger.command"

cat > "$APP_DIR/README-FIRST.txt" <<'EOF'
SiteLedger - Local App Package

How to run:
1. Double-click start-siteledger.command.
2. The app opens at http://127.0.0.1:8081

Requirements:
- Java 17 or newer must be installed.
- Data is stored locally in employee-payroll.db in this same folder.

Default login:
- Username: admin
- Password: change-me-now

Important:
- Change the admin password before real use.
- Keep this whole folder together. Do not delete employee-payroll.db unless you want to remove the local data.
EOF

echo
echo "Portable app package created:"
echo "$APP_DIR"
echo
echo "Give your dad the whole SiteLedger folder."
