package co.zip.candidate.userapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBL_ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}