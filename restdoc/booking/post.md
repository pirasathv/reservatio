# Create a reservation

**URL** : `/api/reservatio/v1/bookings`

**Method** : `POST`

**Auth required** : NONE

Provide reservation information to be created.

**Body example** All fields must be sent.

*user & password*
```json
{
  "email": "email@email.com",
  "firstName": "first",
  "lastName": "last",
  "startDate": "2021-05-07",
  "endDate": "2021-05-10"
}
```

## Success Response

**Condition** : If everything is OK. Reservation UUID will be provided

**Code** : `200 OK`

**Content example**

```json
{
    "uuid": "792af841-cdb2-43c7-b91c-6549b1aeed85"
}
```

## Error Responses

**Condition** : If required field is not provided.

**Code** : `400 Bad Request`

**Content** : ex: `The request content was malformed:
               No usable value for partner
               Did not find value which can be converted into java.lang.String`
