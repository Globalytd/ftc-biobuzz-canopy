# Commit and Author Automation

Date: 2026-09-20
Purpose: Keep class author tags consistent and configure commit identity quickly.

---

## What this automation does

- Automatically adds class-level Javadoc author tags to staged Java files under:
  - `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/**`
- Ensures both names are present in each class Javadoc block:
  - `Daniel Musigire`
  - `Katriel Nakiberu`
- Enforces test documentation quality for files under:
  - `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/**`
- Blocks commit if a test file changes code but the class Javadoc was not updated
- Blocks commit if a test class Javadoc is missing any required plain-English section:
  - `Configuration required`
  - `Controls:`
  - `Safety notes:`

This runs in a Git pre-commit hook, so new classes get tags before commit is created and test docs stay aligned with behavior.

---

## One-time setup (per clone)

From repo root:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
powershell -NoProfile -ExecutionPolicy Bypass -File ".\.githooks\setup-hooks.ps1" -GitUserName "Daniel Musigire" -GitUserEmail "your-email@example.com"
```

If you also want a default co-author trailer template in commit messages:

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
powershell -NoProfile -ExecutionPolicy Bypass -File ".\.githooks\setup-hooks.ps1" -GitUserName "Daniel Musigire" -GitUserEmail "your-email@example.com" -SetCoAuthorTemplate
```

---

## Verify setup

```powershell
Set-Location "C:\Users\DanielMusigire\AndroidStudioProjects\FTC\BioBuzz"
git config --get core.hooksPath
git config --get user.name
git config --get user.email
```

Expected:
- `core.hooksPath` is `.githooks`
- `user.name` is your commit identity
- `user.email` is set

---

## Daily use

No extra commands needed.

When you run `git commit`, the pre-commit hook:
- checks staged Java classes and inserts missing author tags automatically
- re-stages files after auto-fix
- verifies modified test OpModes also update class-level Javadocs

If a test Javadoc check fails, update the class Javadoc text to match the code change and commit again.

---

## Notes about commit authorship

- Git commit **author** is a single identity (`user.name` + `user.email`).
- If you want dual attribution in commit history, use a trailer:

```text
Co-authored-by: Katriel Nakiberu <katriel@example.com>
```

You can keep this trailer in your commit message template with `-SetCoAuthorTemplate`.

---

## Hook files

- `.githooks/pre-commit` (Git hook entrypoint)
- `.githooks/pre-commit.ps1` (author-tag insertion logic)
- `.githooks/setup-hooks.ps1` (one-time repo setup)
