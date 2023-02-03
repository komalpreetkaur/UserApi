package co.zip.candidate.userapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBL_USER")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column
    @NotNull
    @Email
    private String emailId;

    @Column(scale = 2)
    @PositiveOrZero
    private BigDecimal monthlySalary = BigDecimal.ZERO;

    @Column(scale = 2)
    @PositiveOrZero
    private BigDecimal monthlyExpenses = BigDecimal.ZERO;
}