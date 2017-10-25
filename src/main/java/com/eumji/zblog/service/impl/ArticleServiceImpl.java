package com.eumji.zblog.service.impl;

import com.eumji.zblog.task.BaiduTask;
import com.eumji.zblog.util.JsonMapper;
import com.eumji.zblog.vo.Article;
import com.eumji.zblog.cache.service.CacheService;
import com.eumji.zblog.elasticsearch.ArticleSearchRepository;
import com.eumji.zblog.mail.MailService;
import com.eumji.zblog.mapper.ArticleMapper;
import com.eumji.zblog.service.ArticleService;
import com.eumji.zblog.vo.ArticleCustom;
import com.eumji.zblog.vo.Pager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.IteratorUtils;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
/**
 * Created by GeneratorFx on 2017-04-11.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleServiceImpl  implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private BaiduTask baiduTask;
    
    @Resource
    private CacheService cacheService;
    
    @Resource
    private MailService mailService;
    
    @Resource
    private ArticleSearchRepository articleSearchRepository;
    
    @Override
    public List<ArticleCustom> articleList(Pager pager) {
        return articleMapper.getArticleList(pager);
    }

    @Override
    public Pager<Article> InitPager() {
        Pager pager = new Pager();
        int count = articleMapper.getArticleCount();
        pager.setTotalCount(count);
        return pager;
    }

    @Override
    public int getArticleCount() {
        return articleMapper.getArticleCount();
    }

    @Override
    public void InitPager(Pager pager) {
        int count = articleMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Article> loadArticle(Map<String, Object> param) {
    	/*String key=JsonMapper.toJsonString(param);
    	List<Article> list=(List<Article>) cacheService.getObj(key);
    	if(list==null || list.isEmpty()) {
    		list=articleMapper.loadArticle(param);
    		cacheService.set(key, list, 3600L);
    	}*/
    	//mailService.sendSimpleMail("1060920377@qq.com", "邮件服务测试", "dfgjfhdgsffsdfgasdasdasdasdasdas");
    	List<Article> list=articleMapper.loadArticle(param);
        return list ;
    }

    @Override
    public void updateStatue(Integer id, int status) {
        articleMapper.updateStatue(id,status);
    }

    @Override
    public void saveArticle(Article article, int[] tags) throws IOException {
        Integer id  = getRandomId();
        for (int i = 0; i < 50; i++) {
            int count = articleMapper.checkExist(id);
            if (count==0) break;
            else id = getRandomId();
        }
        article.setId(id);
        article.setCreateTime(new Date());
        article.setStatus(1);
        articleMapper.saveArticle(article);
        articleMapper.saveArticleTag(id,tags);
        //保存索引 begin
        articleSearchRepository.save(article);
        //保存索引 end
        baiduTask.pushOneArticle(String.valueOf(id));
    }

    @Override
    public Article getArticleById(Integer id) {
    	/*Article article =cacheService.get(ArticleServiceImpl.class.getName()+"-getArticleById-"+id, Article.class);
    	if(article==null) {
    		article=articleMapper.getArticleById(id);
    		cacheService.set(ArticleServiceImpl.class.getName()+"-getArticleById-"+id, article, 3600L);
    	}*/
    	Article article=articleMapper.getArticleById(id);
        return article;
    }

    @Override
    public void updateArticle(Article article, int[] tags) {
        article.setUpdateTime(new Date());
        articleMapper.updateArticle(article);
        articleMapper.deleteArticleTag(article.getId());
        articleMapper.saveArticleTag(article.getId(),tags);
    }

    @Override
    public void deleteArticle(Integer id) {
    	Article article=articleMapper.getArticleById(id);
    	articleSearchRepository.delete(article);
        articleMapper.deleteArticle(id);
    }

    @Override
    public ArticleCustom getArticleCustomById(Integer articleId) {
    	/*ArticleCustom articleCustom =cacheService.getJson(ArticleServiceImpl.class.getName()+"-getArticleCustomById-"+articleId, ArticleCustom.class);
    	if(articleCustom==null) {
    		articleCustom=articleMapper.getArticleCustomById(articleId);
    		String message=JsonMapper.toJsonString(articleCustom);
    		cacheService.set(ArticleServiceImpl.class.getName()+"-getArticleCustomById-"+articleId, message, 3600L);
    	}*/
    	ArticleCustom articleCustom=articleMapper.getArticleCustomById(articleId);
        return articleCustom;
    }

    @Override
    public Article getLastArticle(Integer articleId) {
        return articleMapper.getLastArticle(articleId);
    }

    @Override
    public Article getNextArticle(Integer articleId) {
        return articleMapper.getNextArticle(articleId);
    }

    @Override
    public void addArticleCount(Integer articleId) {
        articleMapper.addArticleCount(articleId);
    }

    @Override
    public List<ArticleCustom> popularArticle() {
        return articleMapper.popularArticle();
    }

    @Override
    public String[] getArticleId() {
        return articleMapper.getArticleId();
    }

    @Override
    public List<Article> getArticleListByKeywords(String keyword) {
    	//redis 根据关键词查询文章  begin
    	/*List<Article> articleList =(List<Article>) cacheService.getObj(ArticleServiceImpl.class.getName()+"-getArticleListByKeywords-"+keyword);
    	if(articleList==null || articleList.isEmpty()) {
    		articleList=articleMapper.getArticleListByKeywords(keyword);
    		cacheService.set(ArticleServiceImpl.class.getName()+"-getArticleListByKeywords-"+keyword, articleList, 3600L);
    	}*/
    	//redis 根据关键词查询文章  end
    	//elasticsearch 全文检索 根据关键词查询文章  begin
    	QueryStringQueryBuilder builder=new QueryStringQueryBuilder(keyword==null?"":keyword);
    	Iterable<Article> searchResult =articleSearchRepository.search(builder);	
    	Iterator<Article> iterator = searchResult.iterator();
    	List<Article> articleList=IteratorUtils.toList(iterator);
    	//elasticsearch 全文检索 根据关键词查询文章  end
        return articleList;
    }

    @Override
    public List<Map> articleArchiveList() {
        return articleMapper.articleArchiveList();
    }

    private Integer getRandomId(){
        Calendar instance = Calendar.getInstance();
        int month = instance.MONTH;
        int dayOfMonth = instance.DAY_OF_MONTH;
        int random = new Random().nextInt(8999)+1000;
        StringBuilder append = new StringBuilder().append(month).append(dayOfMonth).append(random);

        return Integer.valueOf(append.toString());
    }
}
