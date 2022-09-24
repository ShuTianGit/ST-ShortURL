package vip.stqr.STShortURL.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.stqr.STShortURL.entity.Result;
import vip.stqr.STShortURL.service.UrlService;
import vip.stqr.STShortURL.toolkit.UrlUtils;

/**
 * TODO
 *
 * @author 曙天
 * @since 2022-09-24 11:50
 */
@Controller
public class UrlController {

    @Autowired
    private UrlService urlService;
    private static String host;

    @Value("${server.host}")
    public void setHost(String host) {
        UrlController.host = host;
    }

    @ResponseBody
    @GetMapping("/generate")
    public Result generateShortUrl(@RequestParam String longUrl) {
        if (UrlUtils.checkURL(longUrl)) {
            if (!longUrl.startsWith("http")) {
                longUrl = "http://" + longUrl;
            }
            // String shortURL = urlService.saveUrlMap(HashUtils.hashToBase62(longUrl), longUrl, longUrl);
            String shortURL = urlService.saveUrlMap2(longUrl,"SDLJ11231LKDJF1");
            return Result.ok("请求成功", host + shortURL);
        }
        return Result.create(400, "URL有误");
    }

    @GetMapping("/{shortURL}")
    public String redirect(@PathVariable String shortURL) {
        String longURL = urlService.getLongUrlByShortUrl(shortURL);
        if (longURL != null) {
            urlService.updateUrlViews(shortURL);
            //查询到对应的原始链接，302重定向
            return "redirect:" + longURL;
        }
        //没有对应的原始链接，直接返回首页
        return "redirect:/";
    }
}
