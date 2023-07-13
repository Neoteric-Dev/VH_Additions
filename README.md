# Vault Mod Tweaks

This little companion mod for the Vault mod of the Vault Hunters mod pack fixes a few bugs and brings a couple QoL and balance changes.

All of these can be individually disabled in client & server config files.

If you do not disable the changes that affect expertises, I recommend a full expertise reset after installing this mod.

## Configuration

The mod is configured via a server configuration file. To change settings, do the following:

1. Create a new world and load into it / start your server.
2. Close the world again / stop your server.
3. Find the configuration file in `saves/<world name>/the_vault-tweaks-server.toml` (singleplayer) or `<world name>/the_vault-tweaks-server.toml` (server).
4. Change accordingly. The file is heavily commented.

## Bug Fixes

* Fake players work with research again (e.g. Routers + Botany Pots, AE2 auto crafting).
* Fake players can put Vault Rocks on the Altar again.
* Relic fragments \#5 are now equally as likely to drop as the others.
* AE2 auto crafting no longer requires manually giving its fake player all research.

## Balance Changes

* Removed Emerald cost from Vault Enchanter.
* Vault Enchanters offer Fortune 5, Fortunate expertise removed.
* Jewel cutting has a base 25% chance of failure, Jeweler expertise removed.
* Budding Crystal (Sky Vaults only) growth time reduced.
* Altar infusion time (time between redstone signal and the crystal popping off) reduced.
* Vault Junk Upgrades (Junk Management) tier 1 and 2 buffed
* Jewel cutting buffed from 1–10 size reduction to 3–10.
* Added support for Reaping jewels (you still cannot obtain those without commands or config changes!).
