**English** | [简体中文](README.zh.md)

<p align="center">
  <img src="assets/banner-en.png" alt="AngusRepo — One Repo, All Artifacts" width="100%" />
</p>

<p align="center">
  <a href="https://www.anguskit.com/en/pricing"><img alt="Community Edition" src="https://img.shields.io/badge/Community-Free-2f786c"></a>
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/License-GPL--3.0-blue"></a>
  <a href="https://www.anguskit.com/en/docs/repo"><img alt="Docs" src="https://img.shields.io/badge/docs-anguskit.com-2f786c"></a>
  <a href="https://www.anguskit.com"><img alt="Website" src="https://img.shields.io/badge/website-anguskit.com-c96128"></a>
</p>

# AngusRepo

**One Repo, All Artifacts. Ship Software Faster & Safer.**

Universal Artifact Management — the Ship product in [AngusKit](https://github.com/AngusKit/AngusKit).

> **This repository hosts documentation only.** AngusRepo source code is distributed through private deployment packages, not through this GitHub repository. Earlier revisions of this repository contained application source; as of this update, distribution has moved to AngusKit's packaging pipeline (see [Get the Community Edition](#get-the-community-edition-free) below). This repository now focuses on product information, quickstart guides, and links to the full documentation site.

## What is AngusRepo

AngusRepo hosts 28 mainstream artifact formats (Maven, npm, Docker, PyPI, and more) on one platform, pulling publishing, caching, aggregation, and permissions onto a single control plane — so artifacts stay traceable and governable from build to release.

## Key capabilities

- **Multi-protocol, one repo** — Maven, npm, Docker, PyPI, and more, hosted side by side
- **Protocol-native access** — clients talk to native protocol endpoints, no client-side rewrites
- **ACL & tokens** — fine-grained access control and token-based auth per repository
- **Proxy cache acceleration** — proxy upstream public registries with a local cache
- **Supply-chain security gates** — gate artifact promotion on security findings
- **Private & auditable** — every pull/push stays inside your infrastructure with an audit trail

## Screenshot

<p align="center">
  <img src="assets/screenshot-en.png" alt="AngusRepo console" width="0%" />
</p>

## Get the Community Edition (free)

About **270 MB**. This zip is **AngusGM + AngusRepo**. At least **2 cores / 4 GB** RAM and **100 GB** disk (keep artifacts on a separate disk). Docker Engine + Compose v2.

This first-run path matches the official docs: Docker Compose, the install wizard, **access mode 2** (bundled Caddy), HTTP `:80` (no certificate).

1. Resolve names to this machine (or add to `/etc/hosts` for a local trial). Public DNS name in the wizard is the **suffix** (e.g. `example.com`), not `gm.example.com`.

```bash
127.0.0.1 gm.example.com repo.example.com
```

Open host **80**. App ports stay behind the proxy — do not use `localhost:8801`. Stop Nginx/Caddy/IIS if they already bind 80. On macOS + Docker Desktop, do not run `./install.sh` with `sudo`.

2. Download, unzip, and run the wizard from the package root:

```bash
curl --fail --location --progress-bar -o AngusRepo-Community-1.0.0.zip \
  https://repo.anguskit.com/raw/raw-public/AngusKit/repo/AngusRepo-Community-1.0.0.zip
unzip AngusRepo-Community-1.0.0.zip
cd AngusRepo-1.0.0
./install.sh
```

Answer: Install mode `1` (Compose) → Access **`2`** (bundled reverse proxy — do not press Enter) → Proxy `1` (Caddy) → TLS **`4`** (HTTP `:80`, no certificate) → Database `1` (MySQL 8 in Compose) → Public DNS name = `example.com` → set admin password (default user `admin`). Wait for `Install finished.`

3. Confirm health, then open the console:

```bash
./bin/angusctl.sh doctor
```

Look for `doctor: OK`. Open `http://gm.example.com/`, sign in, then open `http://repo.example.com/`.

Need the full suite? Use `AngusKit-Community-1.0.0.zip` from [AngusKit](https://github.com/AngusKit/AngusKit).

First-run guide: **[repo quickstart](https://www.anguskit.com/en/docs/repo/get-started/quickstart)** · Full install (host ZIP, Helm preview, TLS, offline): **[install docs](https://www.anguskit.com/en/docs/repo/latest/en/manual/02-installation)**

## Community vs. Team / Enterprise vs. SaaS

| | Community | Team / Enterprise | SaaS |
|---|---|---|---|
| Price | Free | Paid, private deployment | Paid, hosted |
| Users | Up to 10 | Higher / unlimited seats | Per plan |
| Artifact repositories | Up to 50 | Higher / unlimited | Per plan |
| Artifact storage | Self-managed | Self-managed or pooled | Per plan |
| Security gates, SBOM, MCP | Not included | Included | Per plan |

Community Edition source is licensed under GPL-3.0 and distributed with each Community installation package. Team and Enterprise editions are proprietary, governed by the **[XCan Business License, Version 1.0](https://www.anguskit.com/licenses/XCBL-1.0)** (XCBL-1.0), distributed only under a paid subscription.

Full pricing and feature comparison: **[anguskit.com/pricing](https://www.anguskit.com/en/pricing)**

## Related AngusKit products

| Product | Focus | Repository |
|---|---|---|
| AngusKit | The full suite (this product + 5 others + AngusGM) | [AngusKit/AngusKit](https://github.com/AngusKit/AngusKit) |
| AngusAI | AI agent development | [AngusKit/AngusAI](https://github.com/AngusKit/AngusAI) |
| AngusGit | AI-native code collaboration | [AngusKit/AngusGit](https://github.com/AngusKit/AngusGit) |
| AngusTester | AI-native software testing | [AngusKit/AngusTester](https://github.com/AngusKit/AngusTester) |
| AngusSecurity | Application security & governance | [AngusKit/AngusSecurity](https://github.com/AngusKit/AngusSecurity) |
| AngusInsight | Private product analytics | [AngusKit/AngusInsight](https://github.com/AngusKit/AngusInsight) |

## Documentation & support

- Full docs: [anguskit.com/docs/repo](https://www.anguskit.com/en/docs/repo)
- Contact / sales: [anguskit.com/contact](https://www.anguskit.com/en/contact) · `sales@anguskit.com`
- This repository's Issues are for **documentation feedback and install troubleshooting**. This repository does not accept source code pull requests — see [CONTRIBUTING.md](CONTRIBUTING.md).

## License

- This repository's documentation content: see [LICENSE](LICENSE) (GPL-3.0, matching the Community Edition source it describes).
- AngusRepo Community Edition product source: GPL-3.0, distributed with each Community installation package.
- AngusRepo Team / Enterprise Edition: proprietary, [XCan Business License, Version 1.0](LICENSE-XCBL-1.0) (XCBL-1.0) — see https://www.anguskit.com/licenses/XCBL-1.0. Distributed under a paid subscription only.
