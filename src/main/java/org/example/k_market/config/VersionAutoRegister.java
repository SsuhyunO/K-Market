package org.example.k_market.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.entity.admin.Version;
import org.example.k_market.repository.admin.VersionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 서버 기동 시(=배포 후 재기동 시) 현재 build.gradle 의 version 값을
 * 버전관리 테이블에 자동으로 등록해주는 컴포넌트. (신규 파일 - config 패키지에 추가)
 *
 * - build.gradle 의 version 값이 바뀐 채로 배포되면 => 새 row 자동 insert
 * - 같은 버전으로 재기동(단순 재시작, 서버 replica 등)되면 => 중복 등록 방지, 스킵
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class VersionAutoRegister implements ApplicationRunner {

    private final VersionRepository versionRepository;

    // build.gradle -> application.properties(spring.application.version) 로 주입된 현재 빌드 버전
    @Value("${spring.application.version}")
    private String currentBuildVersion;

    @Override
    public void run(ApplicationArguments args) {
        if (versionRepository.existsByVersion(currentBuildVersion)) {
            log.info("[VersionAutoRegister] 버전 {} 은 이미 등록되어 있어 자동등록을 스킵합니다.", currentBuildVersion);
            return;
        }

        String id = "V" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        Version version = Version.builder()
                .id(id)
                .version(currentBuildVersion)
                .writerUid("admin") // 배포 시 자동등록이라 로그인 세션이 없음 (member 테이블의 실제 관리자 uid)
                .content("배포 자동 등록 - 변경 내역은 관리자 페이지에서 입력해주세요.")
                .build(); // createdAt 은 @CreationTimestamp 로 자동 세팅

        versionRepository.save(version);
        log.info("[VersionAutoRegister] 새 버전 {} 을 자동 등록했습니다. (id={})", currentBuildVersion, id);
    }
}