package com.eumji.zblog.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.eumji.zblog.vo.Article;

public interface ArticleSearchRepository extends ElasticsearchRepository<Article, Integer>{

}
