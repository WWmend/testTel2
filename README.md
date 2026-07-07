# MultiCwe-DemoApp

Synthetic Spring Boot fixture for multi-CWE regression testing.

Stack: Spring Boot 3.2.5, Java 17, Spring Data JPA + H2, Spring Security.

## Actual engine baseline (2026-06-14, pre-MSG-188)

Live engine run captured at `demos/msg-188/baseline-pre/` against this
fixture with `enabledCwes=["CWE-319","CWE-639"]`:

| Artifact | Count | Where |
|---|---|---|
| `results.sarif` results | **1** | CWE-319 PaymentService.charge:21 (HttpURLConnection.getOutputStream) |
| `detection_results/general/finding_*.json` | **2** | CWE-639 AccountController.getAccount:23 + .deleteAccount:28 |
| `findings_snapshot.json` | **0** | empty (CWE-639 iter overwrote CWE-319's snapshot — known clobber) |

This split is intentional documentation of the **pre-MSG-188 emit gap**:
the trace-pipeline CWE-319 specialist's finding reaches `results.sarif`
via the MSG-183 unified terminal emit, but the CWE-639 specialist runs
through `general_findings_writer` and never populates `pair_findings`,
so its findings only exist on disk at `detection_results/general/`.

MSG-188 closes that gap. Post-MSG-188, expect:

| Artifact | Expected count | Notes |
|---|---|---|
| `results.sarif` results | **3** | 1 CWE-319 + 2 CWE-639 unified |
| `detection_results/general/*.json` | **2** | unchanged |
| `findings_snapshot.json` | **3** | composite key `(pair_key, cwe_token)` |

## Engineered patterns

### CWE-639 — Authorization Bypass / IDOR

| File | Method | Pattern | Confirmed by engine pre-MSG-188? |
|---|---|---|---|
| `controller/AccountController.java` | `getAccount` | `repo.findById(@PathVariable id)`, no ownership check | ✅ confirmed tier=1 conf=high |
| `controller/AccountController.java` | `deleteAccount` | `repo.deleteById(@PathVariable id)`, no ownership check | ✅ confirmed tier=1 conf=high |

### CWE-319 — Cleartext Transmission

| File | Method | Pattern | Confirmed by engine pre-MSG-188? |
|---|---|---|---|
| `service/PaymentService.java` | `charge` | `HttpURLConnection` POST to `http://payments-api.local/charge` | ✅ confirmed |
| `service/AuditService.java` | `emit` | raw `Socket("audit-host.local", 514)` writing token | ❌ engine MISSES this today (separate bug, not MSG-188's scope) |

### CWE-312 — Cleartext Storage of Sensitive Information

| File | Method | Pattern |
|---|---|---|
| `service/CredentialStore.java` | `save` | writes `username:password` to `/var/app/credentials.txt` via `Files.write` — password persisted to disk in cleartext, no encryption |

### Must NOT be flagged (FP-suppression sentinels)

| File | Pattern | Suppression confirmed? |
|---|---|---|
| `controller/SafeAccountController.java` | `findById` + post-fetch ownership check | ✅ rejected by specialist ("After-load ownership check") |
| `service/SafePaymentService.java` | `https://` URL | ✅ not flagged |

## Usage

```bash
cd /Users/waseemweshhi/Workspace/Agentic-SAST
uv run python scripts/msg_188_compare_runs.py capture --label pre
# (apply MSG-188 commits)
uv run python scripts/msg_188_compare_runs.py capture --label post
uv run python scripts/msg_188_compare_runs.py diff
```

Expected `diff` verdict after MSG-188: PASS — post-SARIF is a strict
superset of pre-SARIF (the 2 newly-surfaced CWE-639 entries), and every
result carries the correct `ruleId` with no Safe* file false positives.

## Build

```bash
mvn clean compile -q   # BUILD SUCCESS expected
```

The engine consumes tree-sitter parses of the source tree, not the compiled `.class` files; compile is just a sanity check that the syntax parses.
