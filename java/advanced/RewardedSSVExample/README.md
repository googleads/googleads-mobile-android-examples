# Google AdMob Rewarded Ads Server Side Verification

Server-side verification callbacks are URL requests, with query parameters
expanded by Google, that are sent by Google to an external system to notify it
that a user should be rewarded for interacting with a rewarded video ad.
Rewarded video SSV (server-side verification) callbacks provide an extra layer
of protection against spoofing of client-side callbacks to reward users.

## Description

This project is developed in Java spring-boot framework as an example to verify
rewarded video SSV callbacks by using the Tink third-party cryptographic library
to ensure that the query parameters in the callback are legitimate values.

## How to use

1.  Deploy this project on your preferred web service provider.
2.  Follow the
    [Set up and test server-sideverification](https://support.google.com/admob/answer/9603226)
    instructions to create an ad unit and configure/test your server-side
    verification endpoint.

## Local Development

To start with Java:

1 `cd RewardedSSVExample` 2 `./gradlew bootRun`

To start with Docker:

`docker-compose up --build`

## Local testing

To test a signature and message, send a `GET` request to
`localhost:8080/verify?<dataToVerify>&signature=<signature>&key_id=<key_id>`.

A successful response looks like this:

```
{
  "sig": "ME...Z1c",
  "payload": "ad_network=54...55&ad_unit=12345678&reward_amount=10&reward_item=coins &timestamp=150777823&transaction_id=12...DEF&user_id=1234567",
  "key_id": "1268887",
  "verified": "true"
}
```

