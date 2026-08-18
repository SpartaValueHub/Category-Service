-- sort_order: gap 방식 (1000, 2000, …). UI 순번은 sort_order 값이 아니라 조회 배열 순서.
-- depth 0: 대분류, depth 1: 중분류, depth 2: 브랜드
-- 적용: scripts/reseed-category.cmd 사용 (PowerShell 파이프 넣기 금지 → 한글 ??? 깨짐)
-- mysql 직접 실행 시: --default-character-set=utf8mb4 와 cmd 리다이렉션(<) 필수

USE category_db;

-- 구 테이블 정리 (listing_category는 Listing 서비스로 이전)
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS listing_category;

-- 기존 시드가 있으면 비우고 다시 넣기
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE category;
SET FOREIGN_KEY_CHECKS = 1;

-- ========== depth 0 ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), NULL, 'Luxury', 1000, 0, 1, NOW(6), NULL);
SET @luxury := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), NULL, 'Collectibles', 2000, 0, 1, NOW(6), NULL);
SET @collectibles := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), NULL, 'Premium', 3000, 0, 1, NOW(6), NULL);
SET @premium := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), NULL, 'Electrics', 4000, 0, 1, NOW(6), NULL);
SET @electrics := LAST_INSERT_ID();

-- ========== Luxury depth 1 ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @luxury, '가방', 1000, 1, 1, NOW(6), NULL);
SET @luxury_bag := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @luxury, '패션', 2000, 1, 1, NOW(6), NULL);
SET @luxury_fashion := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @luxury, '신발', 3000, 1, 1, NOW(6), NULL);
SET @luxury_shoes := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @luxury, '주얼리', 4000, 1, 1, NOW(6), NULL);
SET @luxury_jewelry := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @luxury, '시계', 5000, 1, 1, NOW(6), NULL);
SET @luxury_watch := LAST_INSERT_ID();

-- ========== Collectibles depth 1 (브랜드 없음) ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES
(UUID(), @collectibles, '한정판', 1000, 1, 1, NOW(6), NULL),
(UUID(), @collectibles, '피규어', 2000, 1, 1, NOW(6), NULL),
(UUID(), @collectibles, '소장품', 3000, 1, 1, NOW(6), NULL);

-- ========== Premium depth 1 (브랜드 없음) ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES
(UUID(), @premium, '미술품', 1000, 1, 1, NOW(6), NULL),
(UUID(), @premium, '골동품', 2000, 1, 1, NOW(6), NULL);

-- ========== Electrics depth 1 ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @electrics, '카메라', 1000, 1, 1, NOW(6), NULL);
SET @elec_camera := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @electrics, '오디오', 2000, 1, 1, NOW(6), NULL);
SET @elec_audio := LAST_INSERT_ID();

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at)
VALUES (UUID(), @electrics, '노트북/테블릿', 3000, 1, 1, NOW(6), NULL);
SET @elec_laptop := LAST_INSERT_ID();

-- ========== Luxury 브랜드 depth 2 ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @luxury_bag, '샤넬', 1000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '루이비통', 2000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '디올', 3000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '셀린느', 4000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '구찌', 5000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '프라다', 6000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '에르메스', 7000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '생로랑', 8000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '로에베', 9000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_bag, '보테가 베네타', 10000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @luxury_fashion, '루이비통', 1000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '에르메스', 2000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '샤넬', 3000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '구찌', 4000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '디올', 5000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '프라다', 6000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '셀린느', 7000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '로에베', 8000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '펜디', 9000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_fashion, '보테가 베네타', 10000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @luxury_shoes, '크리스찬 루부탱', 1000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '지미추', 2000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '마놀로 블라닉', 3000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '페라가모', 4000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '구찌', 5000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '프라다', 6000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '루이비통', 7000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '샤넬', 8000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '디올', 9000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_shoes, '발렌시아가', 10000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @luxury_jewelry, '티파니앤코', 1000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '까르띠에', 2000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '불가리', 3000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '해리 윈스턴', 4000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '쇼파드', 5000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '반클리프 앤 아펠스', 6000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '부첼라티', 7000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '부쉐론', 8000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '그라프', 9000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '데이비드 율만', 10000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '구찌', 11000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '샤넬', 12000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '에르메스', 13000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_jewelry, '크롬하츠', 14000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @luxury_watch, '롤렉스', 1000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '파텍필립', 2000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '오데마 피게', 3000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '리차드 밀', 4000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '오메가', 5000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '까르띠에', 6000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '바쉐론콘스탄틴', 7000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '예거 르쿨트르', 8000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, 'IWC', 9000, 2, 1, NOW(6), NULL),
(UUID(), @luxury_watch, '브라이틀링', 10000, 2, 1, NOW(6), NULL);

-- ========== Electrics 브랜드 depth 2 ==========
INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @elec_camera, '캐논', 1000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '니콘', 2000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '소니', 3000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '후지필름', 4000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '파나소닉 루믹스', 5000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, 'OM SYSTEM / 올림푸스', 6000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '라이카', 7000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '펜탁스', 8000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, '고프로', 9000, 2, 1, NOW(6), NULL),
(UUID(), @elec_camera, 'DJI', 10000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @elec_audio, '소니', 1000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '보스', 2000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, 'JBL', 3000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '젠하이저', 4000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '마샬', 5000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '하만카돈', 6000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '뱅앤올룹슨', 7000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '야마하', 8000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '오디오테크니카', 9000, 2, 1, NOW(6), NULL),
(UUID(), @elec_audio, '데논', 10000, 2, 1, NOW(6), NULL);

INSERT INTO category (category_uuid, parent_id, category_name, sort_order, depth, active, created_at, deleted_at) VALUES
(UUID(), @elec_laptop, '애플', 1000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '삼성전자', 2000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '마이크로소프트 Surface', 3000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '델', 4000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '레노버', 5000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, 'HP', 6000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '에이수스', 7000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, '레이저', 8000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, 'LG전자', 9000, 2, 1, NOW(6), NULL),
(UUID(), @elec_laptop, 'MSI', 10000, 2, 1, NOW(6), NULL);

SELECT depth, COUNT(*) AS cnt FROM category GROUP BY depth ORDER BY depth;
SELECT category_id, category_name, depth, parent_id, sort_order FROM category WHERE depth = 0 ORDER BY sort_order;
SHOW TABLES;
