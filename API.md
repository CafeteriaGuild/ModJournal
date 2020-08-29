# Proposed API/Schema

## Proposed API

A mod or configuration can register a URL to be fetch on the startup of the game.

The JSON must be either a JSON object or array of objects,
and the objects must follow the Post Schema below.

### Integration with Mods

In, `fabric.mod.json`, add the following to the `custom` property.

```json5
{
    // your other settings...
    "custom": {
        "modjournal:url": "https://example.com/path/to/modid.journal.json"
    }
}
```

### Support for Modpacks

ModJournal adds a configuration file, which can be located at `.minecraft/config/modjournal.json5`

Modpacks are suggested to register their own endpoints as `modjournal.modpack`,
and serverpacks are suggested to register the server's posts as `modjournal.modpack.server` 

```json5
{
	// Custom ModJournals sources
	"customJournals": {
        "modid": "https://example.com/path/to/modid.journal.json",

        // Example for modpacks and serverpacks
        "modjournal.modpack": "https://modpacks.com/modpack/journal.json",
        "modjournal.modpack.server": "https://moddedserver.com/server/journal.json"
    }
}
```

## Proposed Post Schema

```json5
{
    // required
    // always 0
    "schemaVersion": 0,

    // optional if defined by a mod
    // otherwise required
    "modid": "modid",

    // optional
    // must be a URL to a image
    "thumbnail": "",

    // required
    // must be a string or number
    "postid": "first-example-post",

    // optional
    // must be a array of strings
    "authors": [
        "Me!"
    ],

    // optional
    // if it's a number. like 1598662612, it'll be considered a unix time
    // otherwise, it should be a string in the format "YYYY-MM-DD"
    "timestamp": "",

    // required
    // must be a string
    "title": "Example Title",

    // required
    // must be either a string or a array of strings
    // arrays of strings will be concatenated with the line separator
    "content": "This is a content\nit supports line separators.",
}
```