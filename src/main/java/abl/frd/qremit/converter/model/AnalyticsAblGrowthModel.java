package abl.frd.qremit.converter.model;

import javax.persistence.*;


@Entity
@Table(name = "analytics_abl_growth")
public class AnalyticsAblGrowthModel {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_name")
    private String monthName;

    @Column(name = "year")
    private int year;

    @Column(name = "national_amount")
    private Double nationalAmount;

    @Column(name = "abl_amount")
    private Double ablAmount;

    @Column(name = "abl_share")
    private String ablShare;
    
    
}
