package com.rk.ipl.dashboard.data;


import com.rk.ipl.dashboard.model.Match;
import com.rk.ipl.dashboard.model.Team;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final EntityManager em;

    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            getTeamData().values().forEach(em::persist);

        }
    }

    public Map<String, Team> getTeamData() {

//        Generate initial team data
        Map<String, Team> teamData = em.createQuery("select distinct m.team1, count(*) from Match m group by m.team1", Object[].class)
                .getResultStream()
                .map(element -> new Team( (String) element[0], (Long) element[1]))
                .collect(Collectors.toMap(
                        Team::getTeamName,
                        e -> e
                ));

//        Add total matches data for each team
        em.createQuery("select distinct m.team2, count(*) from Match m group by m.team2", Object[].class)
                .getResultStream()
                .forEach(e -> {
                    Team team = teamData.get(String.valueOf(e[0]));
                    team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
                });

//        Add total wins for each team
        em.createQuery("select distinct m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                .getResultStream()
                .forEach(e -> {
                    Team team = teamData.get(String.valueOf(e[0]));
                    if (team != null)
                        team.setTotalWins((long) e[1]);
                });

        return teamData;
    }
}