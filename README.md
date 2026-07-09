## Java Spring Boot — Play-Style Session Cookie

A basic web app demonstrating CWE-312 (cleartext storage of sensitive information) by placing the plaintext password in a `PLAY_SESSION` cookie after authentication.

### Setup (WSL)

```bash
sudo apt update && sudo apt install -y openjdk-17-jdk maven
cd /Context-Aware-SAST/Examples/CWE-312/Java/play-session-cookie
mvn spring-boot:run
```

### Run

App URL: `http://localhost:8083/`

- Open the page, authenticate, then click “Show document.cookie” to view `PLAY_SESSION`.

### cURL examples

- Authenticate (save cookies):
```bash
curl -v -c cookies.txt -H "Content-Type: application/json" \
     --data '{"username":"alice","password":"P@ssw0rd!"}' \
     http://localhost:8083/authenticate
```

- Fetch with cookie:
```bash
curl -v -b cookies.txt http://localhost:8083/me
```

### Observe cleartext in the cookie

`PLAY_SESSION` contains a Base64 (URL-safe) encoded JSON with the plaintext password.

Copy the cookie value (from the browser or `cookies.txt`) into `COOKIE_VAL` and decode:
```bash
COOKIE_VAL='paste_PLAY_SESSION_value_here'
python3 - <<'PY'
import base64, os, json
val = os.environ['COOKIE_VAL']
print(json.dumps(json.loads(base64.urlsafe_b64decode(val + '===').decode()), indent=2))
PY
```
You will see the `password` field in plaintext.

### Remediation (do this in real apps)

- Never store credentials in cookies or client-visible session data.
- If using Play, remember session cookies are signed but not encrypted—treat them as readable.
- Keep secrets server-side; only store non-sensitive identifiers in cookies.
- Use `HttpOnly`, `Secure`, and `SameSite` as appropriate, and minimize cookie contents.

