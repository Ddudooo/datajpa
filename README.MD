# 스프링 데이터 JPA 공부용

스프링 데이터 JPA 활용 JPA 사용

## 정리

### 공통 인터페이스 설정

**JavaConfig 설정**

부트 사용시 생략가능

```java

@Configuration
@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
public class AppConfig {

}
```

* 스프링 부트 사용시 `@SpringBootAppliication` 위치를 지정 해당 패키지와 하위 패키지 인식
* 만약 위치가 달라지면 `@EnableJpaRepositories` 필요

**스프링 데이터 JPA가 구현클래스 생성**

`org.springframework.data.repository.Repository`를 구현한 클래스는 스캔 대상

* `MemberRepository` 인터페이스가 동작한 이유
* `memberRepository.getClass()` -> class `com.sun.proxy.$ProxyXXX`

`@Repository`애노테이션 생략 가능

* 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
* JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리

### 공통 인터페이스

`JpaRepository` 인터페이스

- 공통 CRUD 제공
- 제네릭은 <엔티티 타입, 식별자 타입> 설정

```java
public interface JpaRepository<T, ID extends Serializable>
    extends PagingAndSortingRepository<T, ID> {

}
```

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

}
```

**주의**

- `T findOne(ID)` -> `Optional<T> findById(ID)` 변경

**제네릭 타입**

- `T` 엔티티
- `ID` 엔티티의 식별자 타입
- `S` 엔티티와 그 자식 타입

**주요 메서드**

- `save(S)`  
  새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
- `delete(T)`  
  엔티티 하나를 삭제한다.  
  내부에서 `EntityManager.remove()` 호출
- `findById(ID)`  
  엔티티 하나를 조회한다.  
  내부에서 `EntityManager.find()` 호출
- `getOne(ID)`  
  엔티티를 프록시로 조회한다.  
  내부에서 `EntityManager.getReference()` 호출
- `findAll(…)`  
  모든 엔티티를 조회한다.  
  정렬(`Sort`)이나 페이징(`Pageable`) 조건을 파라미터로 제공할수 있다.

### 쿼리 메소드 기능

공식 가이드 문서 참고

* [Spring data JPA #Query-creation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation)

* 메소드 이름으로 쿼리 생성
* `NamedQuery`
* `@Query` - 리파지토리 메소드에 쿼리 정의
* 파라미터 바인딩
* 반환 타입
* 페이징과 정렬
* 벌크성 수정 쿼리
* `@EntityGraph`

### 확장 기능

**사용자 정의 리포지토리 구현**

* 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
* 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
* 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
    * JPA 직접 사용(`EntityManager`)
    * 스프링 JDBC Template 사용
    * `MyBatis` 사용
    * 데이터베이스 커넥션 직접 사용 등등...
    * `Querydsl` 사용

> 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때  
> 사용자 정의 리포지토리 기능 자주 사용

> 항상 사용자 정의 리포지토리가 필요한 것은 아니다.  
> 그냥 임의의 리포지토리를 만들어도 된다.  
> 예를 들어 `MemberQueryRepository`를 인터페이스가 아닌 클래스로 만들고  
> 스프링 빈으로 등록해서 그냥 직접 사용해도 된다.  
> 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다

스프링 데이터 2.x 부터는  
사용자 정의 구현 클래스에 리포지토리 인터페이스 이름 + Impl 을 적용하는 대신에  
사용자 정의 인터페이스 명 + Impl 방식도 지원한다.

### Auditing

**JPA 사용**
JPA 주요 이벤트 어노테이션

* `@PrePersist`, `@PostPersist`
* `@PreUpdate`, `@PostUpdate`

**스프링 데이터 JPA 사용**

**`@EnableJpaAuditing` 스프링 부트 설정 클래스에 적용해야함**
**`@EntityListeners(AuditingEntityListener.class)` 엔티티에 적용**

**사용 어노테이션**

* `@CreatedDate`
* `@LastModifiedDate`
* `@CreatedBy`
* `@LastModifiedBy`

**전체적용**

`@EntityListeners(AuditingEntityListener.class)` 를 생략하고 스프링 데이터 JPA 가 제공하는 이 벤트를 엔티티 전체에
적용하려면 `orm.xml`에 다음과 같이 등록하면 된다.

```xml
<!--META-INF/orm.xml-->
<?xml version=“1.0” encoding="UTF-8”?>
<entity-mappings xmlns=“http://xmlns.jcp.org/xml/ns/persistence/orm”
    xmlns:xsi=“http://www.w3.org/2001/XMLSchema-instance”
    xsi:schemaLocation=“http://xmlns.jcp.org/xml/ns/persistence/
    orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd”
    version=“2.2">
<persistence-unit-metadata>
<persistence-unit-defaults>
    <entity-listeners>
        <entity-listener
            class="org.springframework.data.jpa.domain.support.AuditingEntityListener”/>
        </entity-listeners>
    </persistence-unit-defaults>
</persistence-unit-metadata>
</entity-mappings>
```

### 스프링 데이터 JPA 구현체

스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체
`org.springframework.data.jpa.repository.support.SimpleJpaRepository`

```java

@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> ...{
@Transactional
public<S extends T> S save(S entity){
    if(entityInformation.isNew(entity)){
    em.persist(entity);
    return entity;
    }else{
    return em.merge(entity);
    }
    }
//...
    }
```

* `@Repository` 적용  
  JPA 예외를 스프링이 추상화한 예외로 변환
* `@Transactional` 트랜잭션 적용
    * JPA의 모든 변경은 트랜잭션 안에서 동작
    * 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리
    * 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
    * 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
    * 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도  
      데이터 등록, 변경이 가능했음(사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임)

* `@Transactional(readOnly = true)`
    * 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 `readOnly = true` 옵션을 사용하면  
      플러시를 생략해서 약간의 성능 향상을 얻을 수 있음

* **`save()` 메서드**
    * 새로운 엔티티면 저장(`persist`)
    * 새로운 엔티티가 아니면 병합(`merge`)

* 새로운 엔티티를 판단하는 기본 전략
    * 식별자가 객체일 때 `null`로 판단
    * 식별자가 자바 기본 타입일 때 `0`으로 판단
    * `Persistable`인터페이스를 구현해서 판단 로직 변경 가능

```java
//Persistable
package org.springframework.data.domain;

public interface Persistable<ID> {

    ID getId();

    boolean isNew();
}
```

> JPA 식별자 생성 전략이 `@GenerateValue`면  
> `save()`호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다.  
> 그런데 JPA 식별자 생성 전략이 `@Id` 만 사용해서 직접 할당이면  
> 이미 식별자 값이 있는 상태로 `save()`를 호출한다.  
> 따라서 이 경우 `merge()`가 호출된다.  
> `merge()`는 우선 DB를 호출해서 값을 확인하고,  
> DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다.  
> 따라서 Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.

> 참고로 등록시간(`@CreatedDate`)을 조합해서 사용하면  
> 이 필드로 새로운 엔티티 여부를 편리하게 확인할수 있다.  
> `@CreatedDate`에 값이 없으면 새로운 엔티티로 판단

### Reference

* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)