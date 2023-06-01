package kr.co.devcs.ggwa.controller

import kr.co.devcs.ggwa.dto.CommentDto
import kr.co.devcs.ggwa.entity.Comment
import kr.co.devcs.ggwa.response.CommentResponse
import kr.co.devcs.ggwa.security.MemberDetails
import kr.co.devcs.ggwa.service.CommentService
import kr.co.devcs.ggwa.service.MeetingService
import kr.co.devcs.ggwa.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/comment")
class CommentController(
    @Autowired private val commentService: CommentService,
    @Autowired private val meetingService: MeetingService,
    @Autowired private val memberService: MemberService
) {
    @GetMapping("/{meeting_id}")
    fun list(@PathVariable meeting_id: Long): ResponseEntity<CommentResponse> {
        if(!meetingService.checkById(meeting_id)) return ResponseEntity.badRequest().body(CommentResponse(mutableListOf(), mutableListOf("비정상적인 접근입니다.")))
        val meeting = meetingService.findById(meeting_id)
        return ResponseEntity.ok().body(CommentResponse(commentService.findByMeeting(meeting)!!, mutableListOf()))
    }

    @PostMapping("/{meeting_id}")
    fun create(@PathVariable meeting_id: Long, @RequestBody commentDto: CommentDto): ResponseEntity<CommentResponse>{
        if(!meetingService.checkById(meeting_id)) return ResponseEntity.badRequest().body(CommentResponse(mutableListOf(), mutableListOf("비정상적인 접근입니다.")))
        if(commentDto.content == "") return ResponseEntity.badRequest().body(CommentResponse(mutableListOf(), mutableListOf("댓글을 작성해주세요.")))
        val memberDetails: MemberDetails = SecurityContextHolder.getContext().authentication.principal as MemberDetails
        val member = memberService.findByEmail(memberDetails.username)
        val meeting = meetingService.findById(meeting_id)
        commentService.create(meeting, member!!, commentDto.content)
        return ResponseEntity.ok().body(CommentResponse(mutableListOf(), mutableListOf()))
    }
}