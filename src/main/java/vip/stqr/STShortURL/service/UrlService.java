package vip.stqr.STShortURL.service;

import org.springframework.scheduling.annotation.Async;

/**
 * TODO
 *
 * @author 曙天
 * @since 2022-09-24 11:58
 */
public interface UrlService {

    String getLongUrlByShortUrl(String shortURL);

    String saveUrlMap(String shortURL, String longURL, String originalURL);
    String saveUrlMap2(String longUrl, String createBy);

    @Async
    void updateUrlViews(String shortURL);

}
