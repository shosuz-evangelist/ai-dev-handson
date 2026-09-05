# セキュリティ要件書 (Security Requirements)

## 概要
EC サイトのセキュリティ要件を定義した文書です。
認証、認可、入力検証、通信暗号化、データ保護に関する要件を記載しています。

---

## 1. 認証 (Authentication)

### 1.1 将来実装予定

現在のトレーニングでは認証機能は実装しませんが、将来的には以下の実装を推奨します。

#### JWT (JSON Web Token) による認証

**エンドポイント**: `POST /api/auth/login`

**リクエスト**:
```json
{
  "username": "user@example.com",
  "password": "SecurePassword123!"
}
```

**レスポンス**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

#### Spring Security 設定

```java
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## 2. 認可 (Authorization)

### 2.1 ロールベースアクセス制御 (RBAC)

将来的に以下のロールを定義予定:

| ロール | 権限 |
|--------|------|
| `ADMIN` | 全ての操作が可能 |
| `USER` | 商品閲覧、カート操作、注文のみ可能 |
| `GUEST` | 商品閲覧のみ可能 |

---

## 3. 入力検証 (Input Validation)

### 3.1 バリデーションアノテーション

すべてのリクエストボディに対して、以下のバリデーションを実施します。

#### Product 登録時のバリデーション

```java
public class ProductRequest {
    @NotNull(message = "商品名は必須です")
    @Size(min = 1, max = 255, message = "商品名は1文字以上255文字以内である必要があります")
    private String name;
    
    @NotNull(message = "価格は必須です")
    @Min(value = 0, message = "価格は0以上である必要があります")
    @Max(value = 999999, message = "価格は999,999円以下である必要があります")
    private Integer price;
    
    @NotNull(message = "画像URLは必須です")
    @Pattern(
        regexp = "^https://.*\\.(jpg|jpeg|png|gif)$",
        message = "有効な画像URLを指定してください"
    )
    private String imageUrl;
}
```

#### Cart 追加時のバリデーション

```java
public class AddToCartRequest {
    @NotNull(message = "商品IDは必須です")
    @Min(value = 1, message = "商品IDは1以上である必要があります")
    private Long productId;
    
    @NotNull(message = "数量は必須です")
    @Min(value = 1, message = "数量は1以上である必要があります")
    @Max(value = 999, message = "数量は999以下である必要があります")
    private Integer quantity;
}
```

### 3.2 グローバル例外ハンドラー

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Validation Error", errors));
    }
}
```

---

## 4. SQLインジェクション対策

### 4.1 JPA の使用

**必須**: 全てのデータベースアクセスに JPA (Spring Data JPA) を使用します。

**禁止事項**:
- 生のSQL文の直接実行
- 文字列連結によるクエリ構築
- `@Query` アノテーションでのパラメータ埋め込み

**推奨実装**:

```java
// ✅ 正しい実装 (JPA Repository)
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String keyword);
    Optional<Product> findById(Long id);
}

// ❌ 間違った実装例
@Query(value = "SELECT * FROM products WHERE name = '" + name + "'", nativeQuery = true)
List<Product> findByNameUnsafe(String name);  // SQLインジェクションの危険性
```

### 4.2 パラメータバインディング

`@Query` を使用する場合は、必ずパラメータバインディングを使用します。

```java
// ✅ 正しい実装
@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
List<Product> searchByKeyword(@Param("keyword") String keyword);

// ❌ 間違った実装
@Query("SELECT p FROM Product p WHERE p.name LIKE '%" + keyword + "%'")
List<Product> searchByKeywordUnsafe(String keyword);
```

---

## 5. XSS (Cross-Site Scripting) 対策

### 5.1 HTML エスケープ

**フロントエンド (React)**:
- React は自動的に XSS 対策を実施
- `dangerouslySetInnerHTML` の使用は禁止

**バックエンド (Spring Boot)**:
- JSON レスポンスは自動的にエスケープされる
- HTML を返す場合は Thymeleaf のエスケープ機能を使用

### 5.2 Content Security Policy (CSP)

```java
@Configuration
public class SecurityHeadersConfig {
    @Bean
    public FilterRegistrationBean<ContentSecurityPolicyFilter> cspFilter() {
        FilterRegistrationBean<ContentSecurityPolicyFilter> registrationBean 
            = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new ContentSecurityPolicyFilter());
        registrationBean.addUrlPatterns("/*");
        
        return registrationBean;
    }
}
```

**CSP ヘッダー**:
```
Content-Security-Policy: 
  default-src 'self'; 
  script-src 'self' 'unsafe-inline'; 
  style-src 'self' 'unsafe-inline'; 
  img-src 'self' https://raw.githubusercontent.com;
```

---

## 6. CORS (Cross-Origin Resource Sharing) 設定

### 6.1 Spring Boot CORS 設定

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")  // React開発サーバー
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

### 6.2 本番環境での設定

本番環境では、環境変数から許可するオリジンを読み込みます。

```java
@Value("${cors.allowed-origins}")
private String[] allowedOrigins;

registry.addMapping("/api/**")
    .allowedOrigins(allowedOrigins)
    // ...
```

---

## 7. 通信暗号化

### 7.1 HTTPS の使用

**本番環境**:
- 全ての通信を HTTPS で実施
- HTTP から HTTPS へのリダイレクト設定
- TLS 1.2 以上を使用

**開発環境**:
- ローカル開発では HTTP を許可
- ただし、本番環境と同じ設定をテスト可能にする

### 7.2 データベース接続の暗号化

**AWS RDS PostgreSQL**:
- SSL/TLS 接続を強制
- `application.properties` での設定:

```properties
spring.datasource.url=jdbc:postgresql://{rds-endpoint}:5432/ecsite?ssl=true&sslmode=require
```

---

## 8. データ保護

### 8.1 個人情報の取り扱い

現在のトレーニングでは個人情報は扱いませんが、将来的に実装する場合の要件:

- パスワードは bcrypt でハッシュ化 (コスト係数: 12以上)
- 個人情報はデータベースで暗号化
- クレジットカード情報は保存しない（決済代行サービスを使用）

### 8.2 ログ出力の制限

**禁止事項**:
- パスワードのログ出力
- トークンのログ出力
- クレジットカード情報のログ出力

**実装例**:

```java
@Slf4j
public class UserService {
    public void login(String username, String password) {
        // ✅ 正しい実装
        log.info("Login attempt for user: {}", username);
        
        // ❌ 間違った実装
        // log.info("Login attempt: username={}, password={}", username, password);
    }
}
```

---

## 9. AWS セキュリティ設定

### 9.1 セキュリティグループ

**最小権限の原則**:
- 必要最小限のポートのみ開放
- ソースIPアドレスを制限

**アプリケーションSG**:
```
インバウンド:
- 80/TCP   from 0.0.0.0/0    (HTTP)
- 443/TCP  from 0.0.0.0/0    (HTTPS)
- 8080/TCP from 0.0.0.0/0    (Spring Boot - 本番ではALB経由に変更)
- 22/TCP   from {Your IP}/32 (SSH管理用)
```

**データベースSG**:
```
インバウンド:
- 5432/TCP from {App SG}      (PostgreSQL)
- 5432/TCP from {Your IP}/32  (管理用接続)
```

### 9.2 RDS セキュリティ

- パブリックアクセス: 無効
- 暗号化: 有効 (AWS KMS)
- 自動バックアップ: 有効 (7日間保持)
- マルチAZ: 本番環境では有効化推奨

---

## 10. 依存関係の脆弱性管理

### 10.1 定期的なアップデート

**Maven 依存関係のチェック**:
```bash
mvn versions:display-dependency-updates
```

**npm 依存関係のチェック**:
```bash
npm audit
npm audit fix
```

### 10.2 脆弱性スキャン

**GitHub Dependabot**:
- 自動的に脆弱性を検出
- プルリクエストで修正提案

---

## 11. セキュリティテスト

### 11.1 テスト項目

- [ ] SQLインジェクション攻撃テスト
- [ ] XSS攻撃テスト
- [ ] CSRF攻撃テスト
- [ ] 不正なリクエストボディのテスト
- [ ] 権限昇格テスト
- [ ] セッション管理テスト

### 11.2 自動セキュリティテスト

```java
@SpringBootTest
class SecurityTests {
    
    @Test
    void testSqlInjectionPrevention() {
        String maliciousInput = "'; DROP TABLE products; --";
        // JPA により自動的に無害化されることを確認
        List<Product> results = productRepository.findByNameContaining(maliciousInput);
        assertNotNull(results);
    }
    
    @Test
    void testXssPrevention() {
        String xssPayload = "<script>alert('XSS')</script>";
        Product product = new Product();
        product.setName(xssPayload);
        // エスケープされて保存・返却されることを確認
        // ...
    }
}
```

---

## 12. セキュリティチェックリスト

### Day 1 実装時

- [ ] 全てのリクエストにバリデーションを実装
- [ ] JPA を使用してSQLインジェクション対策
- [ ] エラーメッセージに機密情報を含めない
- [ ] ログに機密情報を出力しない

### Day 2 実装時

- [ ] CORS 設定を適切に設定
- [ ] RDS のセキュリティグループ設定
- [ ] RDS への SSL/TLS 接続設定
- [ ] CloudWatch でセキュリティイベントを監視

### 本番リリース前

- [ ] HTTPS の有効化
- [ ] 認証・認可の実装
- [ ] CSP ヘッダーの設定
- [ ] セキュリティヘッダーの追加
- [ ] 脆弱性スキャンの実施
- [ ] ペネトレーションテストの実施

---

## 参考資料

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [AWS Security Best Practices](https://aws.amazon.com/security/best-practices/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)

---

## 関連ドキュメント

- [インフラ設計書](./infrastructure-design.md)
- [監視要件書](./monitoring-requirements.md)
