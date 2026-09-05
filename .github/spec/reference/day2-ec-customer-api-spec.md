# Day2 EC Customer API Spec

Base: `/api/customers`

## Model
- id: number
- name: string
- email: string
- createdAt: string (ISO8601)

## Validation
- name: required, 1..255
- email: required, email format, unique

## Endpoints

### GET /api/customers
- Summary: 顧客一覧
- Response: 200

### GET /api/customers/{id}
- Summary: 顧客詳細
- Response: 200 / 404

### POST /api/customers
- Summary: 顧客登録
- Request:
```json
{ "name": "Taro", "email": "taro@example.com" }
```
- Response: 201
- Errors: 400(validation), 409(email duplicated)

### PUT /api/customers/{id}
- Summary: 顧客更新
- Request:
```json
{ "name": "Taro", "email": "taro@example.com" }
```
- Response: 200
- Errors: 400, 404, 409
