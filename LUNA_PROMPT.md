# Luna — Live Zapping Implementation Handoff

You are taking over an unfinished CloudStream Android/Kotlin feature on this fork.

## Repository / branch
- Repository: `Wiojelt/cloudstream-zapping`
- Work ONLY on branch: `live-zapping`
- Inspect the branch itself; do not rely on default-branch code search when it disagrees with branch contents.

## Goal
Finish the Live TV channel-zapping feature completely and leave the branch in a buildable/testable state.

When a user opens a `TvType.Live` channel from a Home category, the player must retain that category's live-channel list and selected index. While the live player is open, DPAD UP/DOWN should switch to the previous/next live channel (with wrap-around) without forcing the user back to Home. Implement sensible debounce/re-entry protection and a small channel-change overlay if the existing player UI architecture supports it cleanly. Do not change normal Movie/TV/Search playback behavior.

## Existing work — inspect before changing
Several commits already introduced partial infrastructure. Do NOT blindly duplicate it. Inspect HEAD/history and reuse, fix, simplify, or remove it as needed:
- `ca884a908af37567cf8ff95dbf353167db9fcdc7` — `ZappingContext` model / wrap-around
- `a627fb5b0bbc4e043701389d3f44d5e380393b87` — transient zapping session store
- `5730b2e4e1a25f5b1bd5e0d4bd2e228b656eef75` — zapping session controller/session logic
- `df15e6ea00653b96115095321110630953940411`, `2723d47773da64c2f20f7242bf74289a11932a1e` — `ZappingPlayerLauncher` integration/fix
- `7e96637a270076ad4f9324352906f9f1e14cb19a` — `PendingZappingStore`
- `ce75dcda9f613c096ca907523a6ae89471fd3bce` — pending context -> player UUID/session bridge
- `08a0a8f478819841df310b980da2be9be8898477` — Home Live-card context capture

Important discovered flow:
`HomeFragment` / Home `SearchAdapter` -> `handleSearchClickCallback()` -> `loadSearchResult(card)` -> `ResultFragment` / `ResultViewModel2` -> `ACTION_PLAY_EPISODE_IN_PLAYER` -> `GeneratorPlayer.newInstance(...)`.

`loadSearchResult` / SearchHelper is shared infrastructure. Keep generic navigation generic; do not make ordinary search/movie/series behavior Live-specific.

## Required implementation
1. First inspect the current `live-zapping` HEAD, all zapping-related files, `HomeFragment`, `ResultFragment`, `ResultViewModel2`, `GeneratorPlayer`, player fragments/views/key handlers, and relevant settings architecture.
2. Verify whether the partial Home -> pending context -> Result/player UUID path is actually wired end-to-end. Fix it rather than creating parallel stores/launchers.
3. Connect the real `ResultViewModel2` player creation path to the zapping-aware launcher/session in the smallest safe change. Do not serialize a large channel list into an Android `Bundle`; keep the actual list transient/in-memory and pass only a small identifier if navigation requires one.
4. Wire DPAD UP/DOWN in the actual player input path for Live zapping only. Preserve existing key behavior for non-Live playback. Respect focus/controller interactions already present in CloudStream.
5. On channel change, update the selected index/session and load/play the target channel through the existing CloudStream result/link/player architecture. Do not invent a second media player. Ensure failure on one channel does not corrupt the session.
6. Add debounce / rapid-key protection so repeated DPAD events cannot create overlapping channel loads. Keep zapping responsive.
7. Add a lightweight overlay showing useful channel information during a switch if it can be implemented consistently with the existing player UI. Auto-hide it. Avoid a large UI rewrite.
8. If this fork already has an appropriate player/settings section, add a minimal Live-zapping enable/disable setting and only apply zapping when enabled. If adding a setting would require invasive unrelated architecture changes, prioritize a correct working implementation and document that decision in the final commit/PR notes instead of hacking it in.
9. Clean up lifecycle state: zapping session/pending state must not leak indefinitely after the relevant player is destroyed/closed. Do not break rotation/process/navigation behavior more than existing transient-state semantics imply.
10. Compile/test. Use the repository's existing Gradle tasks/workflows. At minimum run the relevant Kotlin/Android compile or test task that is feasible in the environment. Fix every compile error caused by this work. Also inspect for imports, nullability, lifecycle, and API mismatches.
11. Review the final diff for unrelated changes and regressions. Keep changes narrowly scoped.
12. Commit the finished implementation to `live-zapping` with clear commit messages. Do not merely describe code that should be written: actually edit, test, and commit it.

## Acceptance criteria
- Opening a Home `TvType.Live` channel establishes a zapping context containing the sibling Live channels in that Home category and the current index.
- In the player, DPAD UP/DOWN moves previous/next with wrap-around.
- Switching channels actually starts the selected target Live channel using CloudStream's existing loading/link/player flow.
- Rapid repeated keys are guarded against overlapping loads.
- Movie/series/non-Live playback is unaffected.
- Session state is cleaned up appropriately.
- Project compiles for the touched code path (or any pre-existing unrelated build failure is clearly distinguished with evidence).
- No giant channel list is shoved into a Bundle.
- No duplicate competing zapping architecture remains unnecessarily.

## Working style
Do not stop after each tiny step to ask for permission. Continue autonomously until the feature is complete or you hit a genuine blocker requiring user input. If you encounter a blocker, first inspect the repository/history/build output and try a safe solution. Keep the implementation idiomatic to the existing project rather than layering a standalone architecture on top.

## Final handoff
When finished, report concisely:
- final commit SHA(s),
- files changed,
- build/test command(s) and result,
- exactly what DPAD UP/DOWN now does,
- any genuine remaining limitation.

Then DELETE this `LUNA_PROMPT.md` file from `live-zapping` as the final cleanup commit, as requested by the user. Do not delete it before you have finished reading and executing the task.