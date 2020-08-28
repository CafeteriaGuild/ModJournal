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

    // required
    // must be a string
    "title": "Example Title",

    // required
    // must be either a string or a array of strings
    // arrays of strings will be concatenated with the line separator
    "content": "This is a content\nit supports line separators.",
}
```