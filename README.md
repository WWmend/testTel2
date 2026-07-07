# cwe-demo

Focused Spring Boot fixture for demonstrating three CWEs in a single scan.

| CWE | Where | Pattern |
|-----|-------|---------|
| CWE-639 (IDOR) | `AccountController.getAccount` | `repo.findById(@PathVariable id)`, no ownership check |
| CWE-639 (IDOR) | `AccountController.deleteAccount` | `repo.deleteById(@PathVariable id)`, no ownership check |
| CWE-319 | `AccountController.charge` | POSTs card number to an `http://` endpoint in cleartext |
| CWE-312 | `AccountController.saveCredentials` | writes the password to disk in cleartext |
