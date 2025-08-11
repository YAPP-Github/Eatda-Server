INSERT INTO member (id, social_id, email, nickname, phone_number, opt_in_marketing)
VALUES (1, 123456789, 'default1@kakao.com', '이승로', '01012345678', true),
       (2, 987654321, 'default2@kakao.com', '이충안', '01087654321', false),
       (3, 456789123, 'default3@kakao.com', '장수빈', '01045678912', true),
       (4, 789123456, 'default4@kakao.com', '서준환', '01078912345', true),
       (5, 321251287, 'default5@kakao.com', '신민선', '01034574568', false),
       (6, 324569987, 'default6@kakao.com', '박희수', '01043609998', false),
       (7, 323487985, 'default7@kakao.com', '하아얀', '01065083298', false);

INSERT INTO store (id, kakao_id, category, phone_number, name, place_url, road_address, lot_number_address, district,
                   latitude, longitude)
VALUES (1, '99999999999', 'KOREAN', '01012345678', '맛있는 한식집', 'https://place.map.kakao.com/17163273',
        '서울시 강남구 역삼동 123-45', '서울시 강남구 역삼동 123-45', 'GANGNAM', 37.503708148482524, 127.05300772497776),
       (2, '99999999998', 'WESTERN', '01087654321', '아름다운 양식집', 'https://place.map.kakao.com/17163273',
        '서울시 강남구 역삼동 67-89', '서울시 강남구 역삼동 67-89', 'GANGNAM', 37.4979, 127.0276),
       (3, '99999999997', 'CHINESE', '01045678912', '정통 중식당', 'https://place.map.kakao.com/17163273',
        '서울시 강남구 역삼동 101-112', '서울시 강남구 역삼동 101-112', 'GANGNAM', 37.56259825108099, 126.97715943361476),
       (4, '99999999996', 'WESTERN', '01078912345', '고급 양식 레스토랑', 'https://place.map.kakao.com/17163273', '',
        '서울시 강남구 역삼동 131-415', 'GANGNAM', 37.4979, 127.0276),
       (5, '99999999995', 'OTHER', '01034574568', '달콤한 디저트 카페', 'https://place.map.kakao.com/17163273',
        '서울시 강남구 역삼동 161-718', '서울시 강남구 역삼동 161-718', 'GANGNAM', 37.49491300989233, 127.03150463098274),
       (6, '99999999994', 'OTHER', '01043609998', '아늑한 커피숍', 'https://place.map.kakao.com/17163273',
        '서울시 강남구 역삼동 192-021', '서울시 강남구 역삼동 192-021', 'GANGNAM', 37.5298343127044, 126.919484339847),
       (7, '99999999993', 'OTHER', '01065083298', '빠른 패스트푸드점', 'https://place.map.kakao.com/17163273', '',
        '서울시 강남구 역삼동 222-324', 'GANGNAM', 37.5036675804016, 127.05305858911);

INSERT INTO cheer (id, member_id, store_id, description, image_key, is_admin)
VALUES (1, 1, 1, '정말 맛있어요! 강추합니다!', 'cheer/dummy/1.jpg', true),
       (2, 2, 2, '서비스가 훌륭해요!', 'cheer/dummy/2.jpg', true),
       (3, 3, 3, '여기 음식이 정말 맛있어요!', 'cheer/dummy/3.jpg', true),
       (4, 4, 4, '분위기가 너무 좋아요!', 'cheer/dummy/4.jpg', true),
       (5, 5, 5, '디저트가 정말 맛있어요!', 'cheer/dummy/5.jpg', true),
       (6, 6, 6, '커피가 정말 맛있어요!', 'cheer/dummy/6.jpg', false),
       (7, 7, 7, '패스트푸드가 빠르고 맛있어요!', 'cheer/dummy/7.jpg', false);

INSERT INTO article (id, title, subtitle, article_url, image_key)
VALUES (1, '첫 번째 기사', '서브타이틀 1', 'https://example.com/article1', 'article/dummy/1.jpg'),
       (2, '두 번째 기사', '서브타이틀 2', 'https://example.com/article2', 'article/dummy/2.jpg'),
       (3, '세 번째 기사', '서브타이틀 3', 'https://example.com/article3', 'article/dummy/3.jpg');
