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

### Flow for user entity creation 

<?xml version="1.0" encoding="us-ascii" standalone="no"?><svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" contentStyleType="text/css" height="366px" preserveAspectRatio="none" style="width:516px;height:366px;background:#FFFFFF;" version="1.1" viewBox="0 0 516 366" width="516px" zoomAndPan="magnify"><defs/><g><text fill="#000000" font-family="sans-serif" font-size="14" font-weight="bold" lengthAdjust="spacing" textLength="122" x="198.75" y="28.5352">Create User Flow</text><line style="stroke:#181818;stroke-width:0.5;stroke-dasharray:5.0,5.0;" x1="23" x2="23" y1="118.9766" y2="285.5293"/><line style="stroke:#181818;stroke-width:0.5;stroke-dasharray:5.0,5.0;" x1="153" x2="153" y1="118.9766" y2="285.5293"/><line style="stroke:#181818;stroke-width:0.5;stroke-dasharray:5.0,5.0;" x1="451.5" x2="451.5" y1="118.9766" y2="285.5293"/><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="31" x="5" y="116.0234">User</text><ellipse cx="23.5" cy="50.9883" fill="#E2E2F0" rx="8" ry="8" style="stroke:#181818;stroke-width:0.5;"/><path d="M23.5,58.9883 L23.5,85.9883 M10.5,66.9883 L36.5,66.9883 M23.5,85.9883 L10.5,100.9883 M23.5,85.9883 L36.5,100.9883 " fill="none" style="stroke:#181818;stroke-width:0.5;"/><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="31" x="5" y="298.0645">User</text><ellipse cx="23.5" cy="309.5176" fill="#E2E2F0" rx="8" ry="8" style="stroke:#181818;stroke-width:0.5;"/><path d="M23.5,317.5176 L23.5,344.5176 M10.5,325.5176 L36.5,325.5176 M23.5,344.5176 L10.5,359.5176 M23.5,344.5176 L36.5,359.5176 " fill="none" style="stroke:#181818;stroke-width:0.5;"/><rect fill="#E2E2F0" height="30.4883" rx="2.5" ry="2.5" style="stroke:#181818;stroke-width:0.5;" width="69" x="119" y="87.4883"/><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="55" x="126" y="108.0234">Porteiro</text><rect fill="#E2E2F0" height="30.4883" rx="2.5" ry="2.5" style="stroke:#181818;stroke-width:0.5;" width="69" x="119" y="284.5293"/><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="55" x="126" y="305.0645">Porteiro</text><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="122" x="387.5" y="116.0234">Porteiro Database</text><path d="M433.5,66.4883 C433.5,56.4883 451.5,56.4883 451.5,56.4883 C451.5,56.4883 469.5,56.4883 469.5,66.4883 L469.5,92.4883 C469.5,102.4883 451.5,102.4883 451.5,102.4883 C451.5,102.4883 433.5,102.4883 433.5,92.4883 L433.5,66.4883 " fill="#E2E2F0" style="stroke:#181818;stroke-width:1.5;"/><path d="M433.5,66.4883 C433.5,76.4883 451.5,76.4883 451.5,76.4883 C451.5,76.4883 469.5,76.4883 469.5,66.4883 " fill="none" style="stroke:#181818;stroke-width:1.5;"/><text fill="#000000" font-family="sans-serif" font-size="14" lengthAdjust="spacing" textLength="122" x="387.5" y="298.0645">Porteiro Database</text><path d="M433.5,311.0176 C433.5,301.0176 451.5,301.0176 451.5,301.0176 C451.5,301.0176 469.5,301.0176 469.5,311.0176 L469.5,337.0176 C469.5,347.0176 451.5,347.0176 451.5,347.0176 C451.5,347.0176 433.5,347.0176 433.5,337.0176 L433.5,311.0176 " fill="#E2E2F0" style="stroke:#181818;stroke-width:1.5;"/><path d="M433.5,311.0176 C433.5,321.0176 451.5,321.0176 451.5,321.0176 C451.5,321.0176 469.5,321.0176 469.5,311.0176 " fill="none" style="stroke:#181818;stroke-width:1.5;"/><polygon fill="#181818" points="141.5,146.2871,151.5,150.2871,141.5,154.2871,145.5,150.2871" style="stroke:#181818;stroke-width:1.0;"/><line style="stroke:#181818;stroke-width:1.0;" x1="23.5" x2="147.5" y1="150.2871" y2="150.2871"/><text fill="#000000" font-family="sans-serif" font-size="13" font-weight="bold" lengthAdjust="spacing" textLength="35" x="30.5" y="145.5449">POST</text><text fill="#000000" font-family="sans-serif" font-size="13" lengthAdjust="spacing" textLength="67" x="69.5" y="145.5449">/api/users</text><polygon fill="#181818" points="439.5,175.5977,449.5,179.5977,439.5,183.5977,443.5,179.5977" style="stroke:#181818;stroke-width:1.0;"/><line style="stroke:#181818;stroke-width:1.0;" x1="153.5" x2="445.5" y1="179.5977" y2="179.5977"/><text fill="#000000" font-family="sans-serif" font-size="13" font-weight="bold" lengthAdjust="spacing" textLength="47" x="160.5" y="174.8555">QUERY</text><text fill="#000000" font-family="sans-serif" font-size="13" lengthAdjust="spacing" textLength="223" x="211.5" y="174.8555">Check if username is already in use</text><polygon fill="#181818" points="439.5,204.9082,449.5,208.9082,439.5,212.9082,443.5,208.9082" style="stroke:#181818;stroke-width:1.0;"/><line style="stroke:#181818;stroke-width:1.0;" x1="153.5" x2="445.5" y1="208.9082" y2="208.9082"/><text fill="#000000" font-family="sans-serif" font-size="13" font-weight="bold" lengthAdjust="spacing" textLength="47" x="160.5" y="204.166">QUERY</text><text fill="#000000" font-family="sans-serif" font-size="13" lengthAdjust="spacing" textLength="196" x="211.5" y="204.166">Check if email is already in use</text><polygon fill="#181818" points="439.5,234.2188,449.5,238.2188,439.5,242.2188,443.5,238.2188" style="stroke:#181818;stroke-width:1.0;"/><line style="stroke:#181818;stroke-width:1.0;" x1="153.5" x2="445.5" y1="238.2188" y2="238.2188"/><text fill="#000000" font-family="sans-serif" font-size="13" font-weight="bold" lengthAdjust="spacing" textLength="47" x="160.5" y="233.4766">INSERT</text><text fill="#000000" font-family="sans-serif" font-size="13" lengthAdjust="spacing" textLength="107" x="211.5" y="233.4766">Insert user entity</text><polygon fill="#181818" points="34.5,263.5293,24.5,267.5293,34.5,271.5293,30.5,267.5293" style="stroke:#181818;stroke-width:1.0;"/><line style="stroke:#181818;stroke-width:1.0;" x1="28.5" x2="152.5" y1="267.5293" y2="267.5293"/><text fill="#000000" font-family="sans-serif" font-size="13" font-weight="bold" lengthAdjust="spacing" textLength="27" x="40.5" y="262.7871">200</text><text fill="#000000" font-family="sans-serif" font-size="13" lengthAdjust="spacing" textLength="68" x="71.5" y="262.7871">User entity</text><!--SRC=[bP2xRW8n44LxVyLe6wILY55AYZI4I3I1GBP8EMiFmOfViMSLyVUn5xI2aj9pZg-lPtPYZdafPRJeUN3sIGKdC_qEv1NDjS7lX4u8Dcn90AmI8HDqcHAysV0do6kuG86AGuAX8ipGLASH4HCRPM99XnWIaqbXnBlheH4Q6NlCD77TtndmUXKQ8SRq-SkaBg5jTzl3Pzk2n6XavLbC3zsOkg_kHxVUVnLvTIBr0-OurlNe24m6jELt-Wp6r_4ZAUJGs8SYDk-7zRxMtlZIW2zh8rzMURwxLUVLVruiYjpTMFy0]--></g></svg>