package com.chatopera.cc.app.persistence.repository;

import com.chatopera.cc.app.model.QuickReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuickReplyRepository extends JpaRepository<QuickReply, String> {
    Page<QuickReply> findByOrgiAndCate(String orgi, String cate, Pageable page) ;

    Page<QuickReply> findByOrgiAndType(String orgi, String type, Pageable page) ;

    List<QuickReply> findByCreater(String creater);

    void deleteByCateAndOrgi(String cate, String orgi) ;

    List<QuickReply> findByOrgiAndCateAndType(String orgi, String cate, String type);

    // public Page<QuickReply> findByQuicktype(String quicktype , int p, int ps) ;

    // public List<QuickReply> findByOrgiAndCreater(String orgi , String creater, String q) ;

    // public Page<QuickReply> findByCateAndUser(String cate , String q , String user , int p, int ps) ;

    //public Page<QuickReply> findByCon(BoolQueryBuilder booleanQueryBuilder , int p, int ps) ;

}
