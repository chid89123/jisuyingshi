package com.imooc.bilibili.api;

import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.*;
import com.imooc.bilibili.service.CollectionGroupService;
import com.imooc.bilibili.service.ElasticSearchService;
import com.imooc.bilibili.service.VideoService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class VideoApi {
    @Autowired
    private VideoService videoService;
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private CollectionGroupService collectionGroupService;
    @Autowired
    private ElasticSearchService elasticSearchService;


    /*
    视频投稿
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        //在es中添加一条数据
         elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    /*
    分页查询视频列表
     */
    @GetMapping("videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size, Integer no, String area) {
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new JsonResponse<>(result);
    }

    /**
     * 视频在线播放
     * @param request
     * @param response
     * @param url
     * @throws Exception
     */
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);
    }

    //点赞视频
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    //取消点赞视频
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    //查询视频点赞数量
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        Map<String, Object> result = videoService.getVideoLikes(videoId, userId);
        return new JsonResponse<>(result);
    }

    //收藏视频
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCollection(videoCollection, userId);
        return JsonResponse.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频收藏数量
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String,Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored) {}
        Map<String, Object> result = videoService.getVideoCollections(videoId, userId);
        return new JsonResponse<>(result);
    }

    //添加收藏分组
    @PostMapping("/video-collections-group")
    public JsonResponse<Long> addVideoCollectionGroup(@RequestBody CollectionGroup collectionGroup) {
        Long userId = userSupport.getCurrentUserId();
        collectionGroup.setUserId(userId);
        Long groupId = collectionGroupService.addVideoCollectionGroup(collectionGroup);
        return new JsonResponse<>(groupId);
    }

    // 获取收藏分组
    @GetMapping("/video-collection-group")
    public JsonResponse<List<CollectionGroup>> getVideoCollectionGroup() {
        Long userId = userSupport.getCurrentUserId();
        List<CollectionGroup> list = collectionGroupService.getVideoCollectionGroup(userId);
        return new JsonResponse<>(list);
    }

    //TODO 收藏分组 collectionGroupService 查询收藏数量需要和收藏分组组合


    /**
     * 视频投币
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频投币数量
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored) {}
        Map<String, Object> result = videoService.getVideoCoins(videoId, userId);
        return new JsonResponse<>(result);
    }


    /**
     * 添加视频评论
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(videoComment, userId);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频评论
     */
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(@RequestParam Integer size,
                                                                 @RequestParam Integer no,
                                                                 @RequestParam Long videoId) {
        PageResult<VideoComment> result = videoService.pageListVideoComments(size, no, videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/video-details")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        Map<String, Object> result = videoService.getVideoDetails(videoId);
        return new JsonResponse<>(result);
    }

    /*
    添加视频观看记录
     */
    @PostMapping("/video-views")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView,
                                             HttpServletRequest request) {
        Long userId;
        try {
            userId = userSupport.getCurrentUserId();
            videoView.setUserId(userId);
            videoService.addVideoView(videoView, request);
        } catch (Exception e) {
            videoService.addVideoView(videoView, request);
        }
        return JsonResponse.success();
    }

    /**
     * 查询视频播放量
     */
    @GetMapping("/video-view-counts")
    public JsonResponse<Integer> getVideoViewCounts(@RequestParam Long videoId) {
        Integer count = videoService.getVideoViewCounts(videoId);
        return new JsonResponse<>(count);
    }

    /**
     * 视频内容推荐
     */
    @GetMapping("/recommendations")
    public JsonResponse<List<Video>> recommend() throws TasteException {
        Long userId = userSupport.getCurrentUserId();
        List<Video> list = videoService.recommend(userId);
        return new JsonResponse<>(list);
    }

    /**
     * 视频帧截取生成黑白剪影
     */
    @GetMapping("/video-frames")
    public JsonResponse<List<VideoBinaryPicture>> captureVideoFrame(@RequestParam Long videoId,
                                                                    @RequestParam String fileMd5) throws Exception {
        List<VideoBinaryPicture> list = videoService.convertVideoToImage(videoId, fileMd5);
        return new JsonResponse<>(list);
    }
}
