# Update sftp settings password

**URL** : `/api/reservatio/v1/bookings/change`

**Method** : `PUT`

**Auth required** : NONE

Change an existing booked reservation

**Data example** Include reservation uuid and the fields described in the example payload.

*change password*
```json
{
  "uuid": "792af841-cdb2-43c7-b91c-6549b1aeed85",
  "email": "update@email.com",
  "firstName": "updatefirst",
  "lastName": "updatelast",
  "startDate": "2021-05-07",
  "endDate": "2021-05-10"
}
```

## Success Response

**Condition** : If everything is OK.

**Code** : `200 OK`

{}

## Error Responses

**Condition** : If required field is not provided.

**Code** : `400 Bad Request`

**Content** : `The request content was malformed:
               No usable value for partner
               Did not find value which can be converted into java.lang.String`
