SCH-PINCÉR
===

[![Trello Board](https://img.shields.io/badge/-Trello%20Board-blue.svg?colorB=0079BF&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4gMfFRg2j85BawAAAJBJREFUSMftk8sNwjAQRGdRukBUwSlOBaEQOqBNqtg+HhciRVYczEZRcvC7rfcz67EsNU4H8ACc/3FgzOfZgoBLugb3czO7/brBRJ+dp5pcjUXFwkjusvebNoF9BABbi+d0wcVewHsW30uFSx8NSTKzYm6NvO+4NwBSFg8RgZANWy3yDfO9xqJnUMS/vY2T8QHlfqEd/I6h/wAAAABJRU5ErkJggg==)
](https://trello.com/b/p5tDOnF8/webschop)

![Screenshot1](https://raw.githubusercontent.com/Gerviba/webschop/master/screenshots/1.png)

![Screenshot3](https://raw.githubusercontent.com/Gerviba/webschop/master/screenshots/3.png)


## Features

- Users can order items
- Auth.SCH support (http://auth.sch.bme.hu)
- Roles: guest, User, GroupLeader, Sysadmin
- Menu for upcoming events
- Customizable circle profiles
- I18N (locale)
- Editable permissions
- Order cancellation
- Items load using ajax
- It uses Thymeleaf as a templating engine
- Other technologies: Spring-boot, JPA, Hibernate, Hibernate search, Maven
- Spring profiles: test, dev, db-mysql, local, production

## API docs

- Open: `/swagger-ui.html`

![DB](https://raw.githubusercontent.com/Gerviba/webschop/master/db_schema.png)

## Additinal docs

### Item settings json

```json
{
    "type": "EXTRA_SELECT",          // Can be: EXTRA_SELECT, EXTRA_CHECKBOX, AMERICANO_EXTRA
    "name": "size",                  // Can be: size, sauce, contain, extra, potato, panzo, type
    "values": ["value1", "value2"],  // Value names
    "prices": [0, 200],              // Additional prices for each value
    "_display": "Display text",      // Display text in search menu (optional, default: values->join) {pieces} => db
    "_hide": true,                   // Hide from search menu (optional, default: false)
    "_comment" : "Lorem ipsum",      // Text below the input field (optional, default: empty string)
    "_extra" : false                 // If true the item will be notified as 'extra'. (optional, default: false)
}
```