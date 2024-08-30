package com.rk.ipl.dashboard.data;

import com.rk.ipl.dashboard.model.Match;
import com.rk.ipl.dashboard.model.MatchInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

@Slf4j
public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> {

    @Override
    public Match process(MatchInput matchInput) throws Exception {
        Match match = new Match();
        match.setId(Long.parseLong(matchInput.getId()));
        match.setCity(matchInput.getCity());
        match.setDate(LocalDate.parse(matchInput.getDate()));
        match.setPlayerOfMatch(matchInput.getPlayerOfMatch());
        match.setVenue(matchInput.getVenue());
        match.setTeam1(
                getFirstInningsTeam(
                        matchInput.getTossDecision(),
                        matchInput.getTossWinner(),
                        matchInput.getTeam1(),
                        matchInput.getTeam2()
                )
        );
        match.setTeam2(
                getSecondInningsTeam(
                        matchInput.getTossDecision(),
                        matchInput.getTossWinner(),
                        matchInput.getTeam1(),
                        matchInput.getTeam2()
                )
        );
        match.setTossDecision(matchInput.getTossDecision());
        match.setTossWinner(matchInput.getTossWinner());
        match.setMatchWinner(matchInput.getWinner());
        match.setResult(matchInput.getResult());
        match.setResultMargin(matchInput.getResultMargin());
        match.setUmpire1(matchInput.getUmpire1());
        match.setUmpire2(matchInput.getUmpire2());
        return match;
    }

    private String getFirstInningsTeam(String tossDecision, String tossWinner, String team1, String team2) {
        if (tossDecision.equals("bat"))
            return tossWinner;
        else
            return tossWinner.equals(team1) ? team2 : team1;
    }

    private String getSecondInningsTeam(String tossDecision, String tossWinner, String team1, String team2) {
        if (tossDecision.equals("bat"))
            return tossWinner.equals(team1) ? team2 : team1;
        else
            return tossWinner;
    }
}
