# Show sftp settings

**URL** : `/api/reservatio/v1/bookings`

**Method** : `GET`

**Auth required** : NONE

## Success Responses

**Condition** : Get a list of available dates for the next 30 days. (default)

**Code** : `200 OK`

**Params** : None

### OR

**Condition** : Get all date within a specified range to get list of available dates

**Code** : `200 OK`

**Params** : `fromDate` and `toDate` 

**Content** : Returns available dates as local dates

```json
{
    "availableDates": "2021-05-12,2021-05-13,2021-05-14,2021-05-15,2021-05-16,2021-05-17,2021-05-18,2021-05-19,2021-05-20,2021-05-23,2021-05-24,2021-05-25,2021-05-26"
}
```

## Error Responses

If invalid date is passed, you will receive a BAD_REQUEST error.