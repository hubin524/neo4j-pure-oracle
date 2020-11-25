package com.tydic.scaffold.batchInsertNeo4j.dao;

import com.tydic.scaffold.batchInsertNeo4j.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QueryNaturalPersonsDao {

    List<NaturalPersonEntity> QueryNaturalPersons(Map<String,Integer> param);

    List<NaturalRelEntity> QueryNaturalRel(Map<String,Integer> param);

    List<UserEntity> QueryUserNode(Map<String,Integer> param);

    List<UserRelEntity> QueryUserRel(Map<String,Integer> param);


    List<FamilyEntity> QueryFamilyNode(Map<String,Integer> param);

    List<LittleFamilyRelEntity> QueryFamilyRel(Map<String,Integer> param);

    List<LittleFamilyEntity> QueryLittleFamilyNode(Map<String,Integer> param);

    List<LittleFamilyRelEntity> QueryLittleFamilyAndFamilyRel(Map<String,Integer> param);
    List<LittleFamilyRelEntity> QueryLittleFamilyAndNaturalRel(Map<String,Integer> param);

    List<EnterpriseEntity> QueryEnterpriseNode(Map<String,Integer> param);

    List<EnterpriseNaturalRelEntity> QueryEnterpriseRel(Map<String,Integer> param);

    List<InternetAccountEntity> QueryQqNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryQqNaturalRel(Map<String,Integer> param);

    List<InternetAccountEntity> QueryEmailNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryEmailNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryweChatNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryweChatNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryJdNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryJdNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryTaobaoNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryTaobaoNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryWeiboNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryWeiboNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryYoukuNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryYoukuNaturalRel(Map<String,Integer> param);


    List<InternetAccountEntity> QueryAqiyiNode(Map<String,Integer> param);

    List<InternetAccountEntity> QueryAqiyiNaturalRel(Map<String,Integer> param);

}
