# Release Process

This document describes the release process for the Strimzi Test Connectors.
The connectors are published to [Maven Central](https://central.sonatype.com/namespace/io.strimzi.strimzi-test-connectors) and consumed by other Strimzi projects:

- [strimzi/test-container-images](https://github.com/strimzi/test-container-images) bundles the `distribution` ZIP into the container images (pinned in `test-connectors.version`)
- [strimzi/strimzi-kafka-operator](https://github.com/strimzi/strimzi-kafka-operator) uses the connector modules in its tests (pinned in `strimzi-test-connectors.version` in `pom.xml`)

This project has its own release cycle, independent of the test-container release.
If a test-container release needs a newer connectors version, release the connectors first.

## Overview

The project uses a **branch-based release strategy** with `release-*` branches.
The process goes: release an RC to Maven Central, announce it and test it in the consuming projects, and once approved, release the GA.

### Quick checklist

At a high level, the release follows these steps:

1. **Create the release branch** and bump the SNAPSHOT version on `main`
2. **Prepare the release branch** (set the version in all `pom.xml` files)
3. **Wait for CI** to pass on the release branch
4. **Release the RC** to Maven Central
5. **Announce the RC**, open bump PRs in Strimzi projects and wait for feedback
6. **Release the GA** to Maven Central once the RC is approved
7. **Post-release:** verify artifacts, create the GitHub release, announce the GA, move the bump PRs to the GA version

## Prerequisites

- Write access to `strimzi/test-connectors`
- Access to the `io.strimzi` namespace in the [Maven Central Portal](https://central.sonatype.com/publishing) to publish deployments

## Step-by-step Release

### 1. Create the release branch (if it does not exist)

#### Branch naming convention

Release branches follow the pattern `release-<major>.<minor>.x`, where the `.x` suffix allows for patch releases from the same branch.
For example:

| Release version |                                 Branch name |
|-----------------|--------------------------------------------:|
| `0.2.0`         |                             `release-0.2.x` |
| `0.3.0`         |                             `release-0.3.x` |
| `0.2.1`         | `release-0.2.x` (reuse the existing branch) |

For a new minor release, create a fresh branch from the tip of `main` and push it to the upstream remote.
For a patch release, the branch already exists, so skip this step.

Right after creating the branch for a new minor release, bump the SNAPSHOT version on `main` to the next minor version (e.g., `0.4.0-SNAPSHOT` once `release-0.3.x` exists):

```bash
git checkout main
RELEASE_VERSION=0.4.0-SNAPSHOT make release_maven
```

From then on `main` may contain changes that will never reach the release being prepared, so it should no longer carry that release's version.

### 2. Prepare the release branch

Set the GA version in all `pom.xml` files on the release branch:

```bash
git checkout release-0.3.x

RELEASE_VERSION=0.3.0 make release_maven

git add pom.xml */pom.xml
git commit -s -m "Prepare for the 0.3.0 release"
git push <repository> release-0.3.x
```

### 3. Wait for CI builds to pass

Make sure the **Build** workflow passes on the release branch, see [Actions](https://github.com/strimzi/test-connectors/actions/workflows/build.yml).

If a build is red, fix the issue on `main` first by creating a PR against `main`, then cherry-pick the fix commit into the release branch:

```bash
git checkout release-0.3.x
git cherry-pick <commit-hash>
git push <repository> release-0.3.x
```

### 4. Release the RC

Trigger the **Release** workflow on the release branch with an RC version.
The workflow sets the version in all `pom.xml` files, builds the project and deploys the modules (parent, `fault-injection-source-connector`, `distribution`) to Maven Central.
The workflow only runs on `release-*` branches.

**GitHub UI:**
1. Go to **Actions** > **Release** > **Run workflow**
2. Select the release branch (e.g., `release-0.3.x`)
3. Enter the release version: `0.3.0-rc1`
4. Click **Run workflow**

Similarly, you can do it via **GitHub CLI:**
```bash
gh workflow run release.yml \
  --repo strimzi/test-connectors \
  --ref release-0.3.x \
  -f releaseVersion=0.3.0-rc1
```

> **Note:** `make release_maven` upper-cases the version, so the RC is published to Maven Central as `0.3.0-RC1`.
> Use the upper-case form wherever the RC is referenced as a Maven version (e.g., in the bump PRs below).

Once the workflow passes, manually publish the artifacts to Maven Central:

1. Go to [Maven Central Publishing](https://central.sonatype.com/publishing)
2. Find the deployment in **Validated** state
3. Click **Publish**
4. Wait ~30 minutes for the artifacts to propagate
5. Verify the RC artifacts are available on [Maven Central](https://central.sonatype.com/namespace/io.strimzi.strimzi-test-connectors)

Then create a tag and a GitHub release for the RC, marking it as a pre-release:

```bash
git checkout release-0.3.x
git tag 0.3.0-rc1
git push <repository> 0.3.0-rc1
```

### 5. Announce and test the RC

Announce the RC:
- Send an announcement to the **cncf-strimzi-users** mailing list
- Post in the **#strimzi** Slack channel

Also open PRs bumping the connectors to the RC version in the Strimzi projects that use them:
- `strimzi/test-container-images`: `test-connectors.version`
- `strimzi/strimzi-kafka-operator`: `strimzi-test-connectors.version` in `pom.xml`

Their CI runs every RC against real usage, and for each new RC the same PRs are updated rather than opening new ones.

If the RC needs fixes, apply them as described in step 3 and release the next RC (`0.3.0-rc2`, ...).

### 6. Release the GA

Once the RC has been tested and approved, trigger the **Release** workflow again with the GA version:

```bash
gh workflow run release.yml \
  --repo strimzi/test-connectors \
  --ref release-0.3.x \
  -f releaseVersion=0.3.0
```

Publish the deployment in the [Maven Central Portal](https://central.sonatype.com/publishing) the same way as for the RC, then tag the GA:

```bash
git checkout release-0.3.x
git tag 0.3.0
git push <repository> 0.3.0
```

### 7. Post-release

- Verify the GA artifacts are available on [Maven Central](https://central.sonatype.com/namespace/io.strimzi.strimzi-test-connectors)
- Create the GitHub release from the GA tag.
  Auto-generated notes list again the PRs that reached the previous release as cherry-picks, so review the notes before publishing
- Announce the GA release on the **cncf-strimzi-users** mailing list and in the **#strimzi** Slack channel
- Update the bump PRs opened during the RC to the GA version (and retitle them), so they can be merged
