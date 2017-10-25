package com.eumji.zblog.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eumji.zblog.cache.service.CacheService;
import com.eumji.zblog.service.ArticleService;
import com.eumji.zblog.service.CategoryService;
import com.eumji.zblog.service.PartnerService;
import com.eumji.zblog.util.JsonMapper;
import com.eumji.zblog.vo.Article;
import com.eumji.zblog.vo.ArticleCustom;
import com.eumji.zblog.vo.CategoryCustom;
import com.eumji.zblog.vo.Partner;

@Component
public class CacheTask {
	@Autowired
	private CacheService cacheService;
	@Autowired
	private ArticleService articleService; //分钟信息的service
	@Autowired
	private PartnerService partnerService;  //友情链接的service
	@Autowired
	private CategoryService categoryService; //分类service
	// @Scheduled(fixedDelay = 200000)
	@Scheduled(cron = "0 0/30 * * * ?")
	public void initCache() {
		 //初始化页面缓存调整
		 List<Map> archiveList=articleService.articleArchiveList();
		 cacheService.set("articleService.articleArchiveList()", archiveList,0L);
		 //缓存文章 ID
		 String [] ids = articleService.getArticleId();
		 if(ids.length>0) {
			 for(String id:ids) {
				 ArticleCustom articleCustom = articleService.getArticleCustomById(Integer.parseInt(id));
				 if(articleCustom!=null) {
					 String message=JsonMapper.toJsonString(articleCustom);
					 cacheService.set("articleService.getArticleCustomById("+id+")",message,0L);
				 }
				 Article article = articleService.getArticleById(Integer.parseInt(id));
				 if(article!=null) {
					 cacheService.set("articleService.getArticleById("+id+")", article,0L);
				 }
				 //上一篇
			     Article lastArticle = articleService.getLastArticle(Integer.parseInt(id));
			     if(lastArticle!=null) {
			    	 cacheService.set("articleService.getLastArticle("+id+")", lastArticle,0L);
			     }
			     //下一篇
			     Article nextArticle = articleService.getNextArticle(Integer.parseInt(id));
			     if(nextArticle!=null) {
			    	 cacheService.set("articleService.getNextArticle("+id+")", nextArticle,0L);
			     }
			 }
		 }
		 //缓存所有的友情链接
		 List<Partner> partnerList = partnerService.findAll();
		 if(partnerList!=null && partnerList.size()>0) {
			 cacheService.set("partnerService.findAll()", partnerList,0L);
		 }
		 //缓存所有的类别
		 List<CategoryCustom> categoryList = categoryService.initCategoryList();
		 if(categoryList!=null && categoryList.size()>0) {
			 cacheService.set("categoryService.initCategoryList()", categoryList,0L);
		 }
	}
}
