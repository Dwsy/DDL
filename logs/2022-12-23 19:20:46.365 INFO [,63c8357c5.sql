2022-12-23 19:20:46.365  INFO [,63c8357c52dd2c80,a93438a024bfb1f3] 15235 --- [nio-7030-exec-4] l.d.d.s.I.UserActiveCommonServiceImpl    : ActiveLogUseMQ:Browse_Article
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlecom0_.deleted=false 
        and articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlefie0_.id as id1_3_0_,
        articlefie0_.create_time as create_t2_3_0_,
        articlefie0_.deleted as deleted3_3_0_,
        articlefie0_.last_modified_time as last_mod4_3_0_,
        articlefie0_.allow_comment as allow_co5_3_0_,
        articlefie0_.article_content_id as article22_3_0_,
        articlefie0_.article_group_id as article23_3_0_,
        articlefie0_.article_source as article_6_3_0_,
        articlefie0_.article_source_url as article_7_3_0_,
        articlefie0_.article_state as article_8_3_0_,
        articlefie0_.banner as banner9_3_0_,
        articlefie0_.code_highlight_style as code_hi10_3_0_,
        articlefie0_.code_highlight_style_dark as code_hi11_3_0_,
        articlefie0_.collect_num as collect12_3_0_,
        articlefie0_.comment_num as comment13_3_0_,
        articlefie0_.down_num as down_nu14_3_0_,
        articlefie0_.mark_down_theme as mark_do15_3_0_,
        articlefie0_.mark_down_theme_dark as mark_do16_3_0_,
        articlefie0_.summary as summary17_3_0_,
        articlefie0_.title as title18_3_0_,
        articlefie0_.up_num as up_num19_3_0_,
        articlefie0_.user_id as user_id24_3_0_,
        articlefie0_.version as version20_3_0_,
        articlefie0_.view_num as view_nu21_3_0_,
        articlegro1_.id as id1_4_1_,
        articlegro1_.create_time as create_t2_4_1_,
        articlegro1_.deleted as deleted3_4_1_,
        articlegro1_.last_modified_time as last_mod4_4_1_,
        articlegro1_.article_num as article_5_4_1_,
        articlegro1_.info as info6_4_1_,
        articlegro1_.name as name7_4_1_,
        articletag2_.article_field_id as article_1_6_2_,
        articletag3_.id as article_2_6_2_,
        articletag3_.id as id1_5_3_,
        articletag3_.create_time as create_t2_5_3_,
        articletag3_.deleted as deleted3_5_3_,
        articletag3_.last_modified_time as last_mod4_5_3_,
        articletag3_.article_group_id as article10_5_3_,
        articletag3_.article_num as article_5_5_3_,
        articletag3_.index_page_display as index_pa6_5_3_,
        articletag3_.name as name7_5_3_,
        articletag3_.tag_info as tag_info8_5_3_,
        articletag3_.weight as weight9_5_3_,
        articlegro4_.id as id1_4_4_,
        articlegro4_.create_time as create_t2_4_4_,
        articlegro4_.deleted as deleted3_4_4_,
        articlegro4_.last_modified_time as last_mod4_4_4_,
        articlegro4_.article_num as article_5_4_4_,
        articlegro4_.info as info6_4_4_,
        articlegro4_.name as name7_4_4_,
        user5_.id as id1_36_5_,
        user5_.create_time as create_t2_36_5_,
        user5_.deleted as deleted3_36_5_,
        user5_.last_modified_time as last_mod4_36_5_,
        user5_.area as area5_36_5_,
        user5_.email as email6_36_5_,
        user5_.experience as experien7_36_5_,
        user5_.level as level8_36_5_,
        user5_.nickname as nickname9_36_5_,
        user5_.password as passwor10_36_5_,
        user5_.phone as phone11_36_5_,
        user5_.salt as salt12_36_5_,
        user5_.user_info_id as user_in14_36_5_,
        user5_.username as usernam13_36_5_,
        userinfo6_.id as id1_29_6_,
        userinfo6_.create_time as create_t2_29_6_,
        userinfo6_.deleted as deleted3_29_6_,
        userinfo6_.last_modified_time as last_mod4_29_6_,
        userinfo6_.avatar as avatar5_29_6_,
        userinfo6_.birth as birth6_29_6_,
        userinfo6_.gender as gender7_29_6_,
        userinfo6_.sign as sign8_29_6_ 
    from
        article_field articlefie0_ 
    left outer join
        article_group articlegro1_ 
            on articlefie0_.article_group_id=articlegro1_.id 
    left outer join
        article_tag_ref articletag2_ 
            on articlefie0_.id=articletag2_.article_field_id 
    left outer join
        article_tag articletag3_ 
            on articletag2_.article_tag_id=articletag3_.id 
    left outer join
        article_group articlegro4_ 
            on articletag3_.article_group_id=articlegro4_.id 
    left outer join
        users user5_ 
            on articlefie0_.user_id=user5_.id 
    left outer join
        user_info userinfo6_ 
            on user5_.user_info_id=userinfo6_.id 
    where
        articlefie0_.id=?
Hibernate: 
    select
        user0_.id as id1_36_0_,
        user0_.create_time as create_t2_36_0_,
        user0_.deleted as deleted3_36_0_,
        user0_.last_modified_time as last_mod4_36_0_,
        user0_.area as area5_36_0_,
        user0_.email as email6_36_0_,
        user0_.experience as experien7_36_0_,
        user0_.level as level8_36_0_,
        user0_.nickname as nickname9_36_0_,
        user0_.password as passwor10_36_0_,
        user0_.phone as phone11_36_0_,
        user0_.salt as salt12_36_0_,
        user0_.user_info_id as user_in14_36_0_,
        user0_.username as usernam13_36_0_,
        userinfo1_.id as id1_29_1_,
        userinfo1_.create_time as create_t2_29_1_,
        userinfo1_.deleted as deleted3_29_1_,
        userinfo1_.last_modified_time as last_mod4_29_1_,
        userinfo1_.avatar as avatar5_29_1_,
        userinfo1_.birth as birth6_29_1_,
        userinfo1_.gender as gender7_29_1_,
        userinfo1_.sign as sign8_29_1_ 
    from
        users user0_ 
    left outer join
        user_info userinfo1_ 
            on user0_.user_info_id=userinfo1_.id 
    where
        user0_.id=?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlefie0_.id as id1_3_0_,
        articlefie0_.create_time as create_t2_3_0_,
        articlefie0_.deleted as deleted3_3_0_,
        articlefie0_.last_modified_time as last_mod4_3_0_,
        articlefie0_.allow_comment as allow_co5_3_0_,
        articlefie0_.article_content_id as article22_3_0_,
        articlefie0_.article_group_id as article23_3_0_,
        articlefie0_.article_source as article_6_3_0_,
        articlefie0_.article_source_url as article_7_3_0_,
        articlefie0_.article_state as article_8_3_0_,
        articlefie0_.banner as banner9_3_0_,
        articlefie0_.code_highlight_style as code_hi10_3_0_,
        articlefie0_.code_highlight_style_dark as code_hi11_3_0_,
        articlefie0_.collect_num as collect12_3_0_,
        articlefie0_.comment_num as comment13_3_0_,
        articlefie0_.down_num as down_nu14_3_0_,
        articlefie0_.mark_down_theme as mark_do15_3_0_,
        articlefie0_.mark_down_theme_dark as mark_do16_3_0_,
        articlefie0_.summary as summary17_3_0_,
        articlefie0_.title as title18_3_0_,
        articlefie0_.up_num as up_num19_3_0_,
        articlefie0_.user_id as user_id24_3_0_,
        articlefie0_.version as version20_3_0_,
        articlefie0_.view_num as view_nu21_3_0_,
        articlegro1_.id as id1_4_1_,
        articlegro1_.create_time as create_t2_4_1_,
        articlegro1_.deleted as deleted3_4_1_,
        articlegro1_.last_modified_time as last_mod4_4_1_,
        articlegro1_.article_num as article_5_4_1_,
        articlegro1_.info as info6_4_1_,
        articlegro1_.name as name7_4_1_,
        articletag2_.article_field_id as article_1_6_2_,
        articletag3_.id as article_2_6_2_,
        articletag3_.id as id1_5_3_,
        articletag3_.create_time as create_t2_5_3_,
        articletag3_.deleted as deleted3_5_3_,
        articletag3_.last_modified_time as last_mod4_5_3_,
        articletag3_.article_group_id as article10_5_3_,
        articletag3_.article_num as article_5_5_3_,
        articletag3_.index_page_display as index_pa6_5_3_,
        articletag3_.name as name7_5_3_,
        articletag3_.tag_info as tag_info8_5_3_,
        articletag3_.weight as weight9_5_3_,
        articlegro4_.id as id1_4_4_,
        articlegro4_.create_time as create_t2_4_4_,
        articlegro4_.deleted as deleted3_4_4_,
        articlegro4_.last_modified_time as last_mod4_4_4_,
        articlegro4_.article_num as article_5_4_4_,
        articlegro4_.info as info6_4_4_,
        articlegro4_.name as name7_4_4_,
        user5_.id as id1_36_5_,
        user5_.create_time as create_t2_36_5_,
        user5_.deleted as deleted3_36_5_,
        user5_.last_modified_time as last_mod4_36_5_,
        user5_.area as area5_36_5_,
        user5_.email as email6_36_5_,
        user5_.experience as experien7_36_5_,
        user5_.level as level8_36_5_,
        user5_.nickname as nickname9_36_5_,
        user5_.password as passwor10_36_5_,
        user5_.phone as phone11_36_5_,
        user5_.salt as salt12_36_5_,
        user5_.user_info_id as user_in14_36_5_,
        user5_.username as usernam13_36_5_,
        userinfo6_.id as id1_29_6_,
        userinfo6_.create_time as create_t2_29_6_,
        userinfo6_.deleted as deleted3_29_6_,
        userinfo6_.last_modified_time as last_mod4_29_6_,
        userinfo6_.avatar as avatar5_29_6_,
        userinfo6_.birth as birth6_29_6_,
        userinfo6_.gender as gender7_29_6_,
        userinfo6_.sign as sign8_29_6_ 
    from
        article_field articlefie0_ 
    left outer join
        article_group articlegro1_ 
            on articlefie0_.article_group_id=articlegro1_.id 
    left outer join
        article_tag_ref articletag2_ 
            on articlefie0_.id=articletag2_.article_field_id 
    left outer join
        article_tag articletag3_ 
            on articletag2_.article_tag_id=articletag3_.id 
    left outer join
        article_group articlegro4_ 
            on articletag3_.article_group_id=articlegro4_.id 
    left outer join
        users user5_ 
            on articlefie0_.user_id=user5_.id 
    left outer join
        user_info userinfo6_ 
            on user5_.user_info_id=userinfo6_.id 
    where
        articlefie0_.id=?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
Hibernate: 
    select
        articlecom0_.id as id1_0_,
        articlecom0_.create_time as create_t2_0_,
        articlecom0_.deleted as deleted3_0_,
        articlecom0_.last_modified_time as last_mod4_0_,
        articlecom0_.article_field_id as article14_0_,
        articlecom0_.comment_serial_number as comment_5_0_,
        articlecom0_.comment_type as comment_6_0_,
        articlecom0_.down_num as down_num7_0_,
        articlecom0_.parent_comment_id as parent_c8_0_,
        articlecom0_.parent_user_id as parent_u9_0_,
        articlecom0_.reply_user_comment_id as reply_u10_0_,
        articlecom0_.text as text11_0_,
        articlecom0_.ua as ua12_0_,
        articlecom0_.up_num as up_num13_0_,
        articlecom0_.user_id as user_id15_0_ 
    from
        article_comment articlecom0_ 
    left outer join
        article_field articlefie1_ 
            on articlecom0_.article_field_id=articlefie1_.id 
    where
        articlefie1_.id=? 
        and articlecom0_.parent_comment_id=? 
        and articlecom0_.comment_type=? 
        and articlecom0_.deleted=false 
    order by
        articlecom0_.create_time asc limit ?
