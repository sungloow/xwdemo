package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.common.UserContext;
import com.citycheckin.common.exception.BusinessException;
import com.citycheckin.dto.CheckinAddDTO;
import com.citycheckin.dto.RankItemDTO;
import com.citycheckin.dto.ReviewDTO;
import com.citycheckin.entity.Checkin;
import com.citycheckin.entity.CheckinComment;
import com.citycheckin.entity.CheckinLike;
import com.citycheckin.entity.ScenicSpot;
import com.citycheckin.mapper.CheckinCommentMapper;
import com.citycheckin.mapper.CheckinLikeMapper;
import com.citycheckin.mapper.CheckinMapper;
import com.citycheckin.service.CheckinService;
import com.citycheckin.service.ScenicSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckinMapper, Checkin> implements CheckinService {

    private static final int MAX_DAILY_CHECKIN = 10;
    private final ScenicSpotService scenicSpotService;
    private final CheckinLikeMapper checkinLikeMapper;
    private final CheckinCommentMapper checkinCommentMapper;

    @Override
    public void addCheckin(CheckinAddDTO dto) {
        Integer userId = UserContext.getUserId();
        int todayCount = baseMapper.countTodayCheckins(userId);
        if (todayCount >= MAX_DAILY_CHECKIN) {
            throw new BusinessException(400, "今日打卡次数已达上限（" + MAX_DAILY_CHECKIN + "次）");
        }
        if (dto.getDistrictId() == null) {
            throw new BusinessException(400, "请选择区县");
        }
        if (dto.getType() == null || dto.getType().isEmpty()) {
            throw new BusinessException(400, "请选择打卡类型（food/scenic）");
        }
        if ("food".equals(dto.getType()) && dto.getFoodTypeId() == null) {
            throw new BusinessException(400, "美食打卡请选择美食种类");
        }
        if ("scenic".equals(dto.getType()) && dto.getScenicSpotId() == null) {
            throw new BusinessException(400, "风景打卡请选择风景地");
        }
        if ("scenic".equals(dto.getType())) {
            ScenicSpot scenicSpot = scenicSpotService.getById(dto.getScenicSpotId());
            if (scenicSpot == null) {
                throw new BusinessException(404, "所选景点不存在");
            }
            if (!dto.getDistrictId().equals(scenicSpot.getDistrictId())) {
                throw new BusinessException(400, "所选景点与所属区县不匹配");
            }
        }

        Checkin checkin = new Checkin();
        checkin.setUserId(userId);
        checkin.setDistrictId(dto.getDistrictId());
        checkin.setType(dto.getType());
        checkin.setFoodTypeId(dto.getFoodTypeId());
        checkin.setScenicSpotId(dto.getScenicSpotId());
        checkin.setTitle(dto.getTitle());
        checkin.setContent(dto.getContent());
        checkin.setImages(dto.getImages());
        checkin.setAddress(dto.getAddress());
        checkin.setLatitude(dto.getLatitude());
        checkin.setLongitude(dto.getLongitude());
        checkin.setStatus(0);
        checkin.setCreateTime(LocalDateTime.now());
        checkin.setUpdateTime(LocalDateTime.now());
        save(checkin);
    }

    @Override
    public Page<Checkin> pageCheckins(int current, int size, Integer districtId, String type, Integer status, Integer userId) {
        Page<Checkin> page = new Page<>(current, size);
        return baseMapper.pageCheckins(page, districtId, type, status, userId);
    }

    @Override
    public void review(ReviewDTO dto) {
        Checkin checkin = getById(dto.getCheckinId());
        if (checkin == null) throw new BusinessException(404, "打卡记录不存在");

        // 区县管理员只能审核自己区县的内容
        if (!UserContext.isSuperAdmin()) {
            Integer adminDistrictId = UserContext.getDistrictId();
            if (adminDistrictId != null && !adminDistrictId.equals(checkin.getDistrictId())) {
                throw new BusinessException(403, "无权审核其他区县的内容");
            }
        }
        if (dto.getStatus() != 1 && dto.getStatus() != 2) {
            throw new BusinessException(400, "审核状态只能为 1（通过）或 2（拒绝）");
        }
        checkin.setStatus(dto.getStatus());
        if (dto.getStatus() == 2) {
            checkin.setRejectReason(dto.getRejectReason());
        }
        checkin.setUpdateTime(LocalDateTime.now());
        updateById(checkin);
    }

    @Override
    public List<RankItemDTO> districtRank(int limit) {
        return baseMapper.districtRank(limit);
    }

    @Override
    public List<RankItemDTO> foodTypeRank(int limit) {
        return baseMapper.foodTypeRank(limit);
    }

    @Override
    public List<RankItemDTO> scenicSpotRank(int limit) {
        return baseMapper.scenicSpotRank(limit);
    }

    @Override
    public Checkin getPublishedDetail(Integer id, Integer currentUserId) {
        Checkin checkin = baseMapper.selectPublishedDetailById(id);
        if (checkin == null) {
            throw new BusinessException(404, "打卡内容不存在或未发布");
        }
        boolean liked = false;
        if (currentUserId != null) {
            liked = checkinLikeMapper.selectCount(new LambdaQueryWrapper<CheckinLike>()
                    .eq(CheckinLike::getCheckinId, id)
                    .eq(CheckinLike::getUserId, currentUserId)) > 0;
        }
        checkin.setLiked(liked);
        return checkin;
    }

    @Override
    public List<CheckinComment> listPublishedComments(Integer checkinId) {
        ensurePublished(checkinId);
        return checkinCommentMapper.selectByCheckinId(checkinId);
    }

    @Override
    public void likePublishedCheckin(Integer checkinId, Integer userId) {
        ensurePublished(checkinId);
        long exists = checkinLikeMapper.selectCount(new LambdaQueryWrapper<CheckinLike>()
                .eq(CheckinLike::getCheckinId, checkinId)
                .eq(CheckinLike::getUserId, userId));
        if (exists > 0) return;

        CheckinLike like = new CheckinLike();
        like.setCheckinId(checkinId);
        like.setUserId(userId);
        like.setCreateTime(LocalDateTime.now());
        checkinLikeMapper.insert(like);
    }

    @Override
    public void unlikePublishedCheckin(Integer checkinId, Integer userId) {
        ensurePublished(checkinId);
        checkinLikeMapper.delete(new LambdaQueryWrapper<CheckinLike>()
                .eq(CheckinLike::getCheckinId, checkinId)
                .eq(CheckinLike::getUserId, userId));
    }

    @Override
    public void addComment(Integer checkinId, Integer userId, String content) {
        ensurePublished(checkinId);
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(400, "评论内容不能为空");
        }
        String normalized = content.trim();
        if (normalized.length() > 200) {
            throw new BusinessException(400, "评论内容不能超过200字");
        }
        CheckinComment comment = new CheckinComment();
        comment.setCheckinId(checkinId);
        comment.setUserId(userId);
        comment.setContent(normalized);
        comment.setCreateTime(LocalDateTime.now());
        checkinCommentMapper.insert(comment);
    }

    private void ensurePublished(Integer checkinId) {
        Checkin checkin = getById(checkinId);
        if (checkin == null || !Integer.valueOf(1).equals(checkin.getStatus())) {
            throw new BusinessException(404, "打卡内容不存在或未发布");
        }
    }
}
