package com.cruru.club.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cruru.club.domain.Club;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("동아리 레포지토리 테스트")
@DataJpaTest
class ClubRepositoryTest {

    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    void setUp() {
        clubRepository.deleteAllInBatch();
    }

    @DisplayName("이미 DB에 저장되어 있는 ID를 가진 동아리를 저장하면, 해당 ID의 동아리는 후에 작성된 정보로 업데이트한다.")
    @Test
    void sameIdUpdate() {
        //given
        Club club = new Club("써밋", null);
        Club saved = clubRepository.save(club);

        //when
        Club updateClub = new Club(saved.getId(), "크루루", null);
        clubRepository.save(updateClub);

        //then
        Club findClub = clubRepository.findById(saved.getId()).get();
        assertThat(findClub.getName()).isEqualTo("크루루");
    }

    @DisplayName("ID가 없는 동아리를 저장하면, ID를 순차적으로 부여하여 저장한다.")
    @Test
    void saveNoId() {
        //given
        Club club1 = new Club("써밋", null);
        Club club2 = new Club("크루루", null);

        //when
        Club savedClub1 = clubRepository.save(club1);
        Club savedClub2 = clubRepository.save(club2);

        //then
        assertThat(savedClub1.getId() + 1).isEqualTo(savedClub2.getId());
    }
}