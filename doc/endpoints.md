# Endpoints

## `POST /api/users`

This endpoint allow us to request the creation of a new user entity.

Expected body input:

```json
{
  "username": "manuel-gomes",
  "email": "manuel-gomes@example.com",
  "password": "BlUePeN"
}
```

The successful response for a user creation request is the following:

`STATUS 201`

```json
{
  "user": {
    "id": "random-uuid",
    "email": "manuel-gomes@example.com",
    "username": "manuel-gomes",
    "roles": []
  }
}
```

This endpoint checks if the username is already in use, if that happens you can expect this as answer:

`STATUS 409`

```json
{
  "detail": "username already in use by other user",
  "error": "not unique",
  "message": "Username already in use"
}
```

We have a similar check for the email property, it should be unique.
If the email is already in use you can expect the following error response:

`STATUS 409`

```json
{
  "detail": "Email already in use by other user",
  "error": "not unique",
  "message": "Email already in use"
}
```