package vip.stqr.STShortURL.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.stqr.STShortURL.entity.Result;
import vip.stqr.STShortURL.service.UrlService;
import vip.stqr.STShortURL.toolkit.UrlUtils;

/**
 * 长链接转短链接控制器
 *
 * @author 曙天
 * @since 2022-09-24 11:50
 */
@Tag(name = "长链接转短链接")
@Controller
public class UrlController {

    @Autowired
    private UrlService urlService;
    private static String host;

    @Value("${server.host}")
    public void setHost(String host) {
        UrlController.host = host;
    }
    @Operation(summary = "长链接转短链接")
    @ResponseBody
    @PostMapping("/generate")
    public Result generateShortUrl(@RequestParam String longUrl, @RequestParam String createBy) {
        if (UrlUtils.checkURL(longUrl)) {
            if (!longUrl.startsWith("http")) {
                longUrl = "http://" + longUrl;
            }
            String shortURL = urlService.saveUrlMap(longUrl,createBy);
            return Result.ok("请求成功", host + shortURL);
        }
        return Result.create(400, "URL有误");
    }
    @Operation(summary = "短链接重定向")
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
