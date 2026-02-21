cherry-domain
├─ auth
│   ├─ entity
│   │     User
│   │     Role
│   │     UserRole
│   │     UserDevice
│   │     RefreshToken
│   │     LoginLog
│   ├─ dto
│   │     LoginRequest
│   │     RefreshRequest
│   ├─ vo
│   │     LoginResponse
│   │     DeviceVO
│
├─ blog
│   ├─ entity
│   │     BlogArticle
│   │     BlogCategory
│   │     BlogTag
│   │     BlogArticleTag
│   ├─ dto
│   │     CreateArticleRequest
│   │     UpdateArticleRequest
│   ├─ vo
│   │     ArticleDetailVO
│   │     ArticleListVO
│
├─ common
│   ├─ result
│   │     Result
│   ├─ enums
│   │     UserStatusEnum
│   │     DeviceTypeEnum
│   ├─ constant
│         RedisKeyConstant
