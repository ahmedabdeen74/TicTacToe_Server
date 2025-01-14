/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import dto.DTOPlayer;
import java.sql.Timestamp;

/**
 *
 * @author Al Badr
 */
public class Game {
    private int id;
    private DTOPlayer from_player;
    private DTOPlayer to_player;
    private DTOPlayer winner;
    private Timestamp created_at;
    private GameStatus status;
    private String board;

    public Game() {
    }

    public Game(int id, DTOPlayer from_player, DTOPlayer to_player, DTOPlayer winner, Timestamp created_at, GameStatus status, String board) {
        this.id = id;
        this.from_player = from_player;
        this.to_player = to_player;
        this.winner = winner;
        this.created_at =  new Timestamp(System.currentTimeMillis());
        this.status = status;
        this.board = board;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DTOPlayer getFrom_player() {
        return from_player;
    }

    public void setFrom_player(DTOPlayer from_player) {
        this.from_player = from_player;
    }

    public DTOPlayer getTo_player() {
        return to_player;
    }

    public void setTo_player(DTOPlayer to_player) {
        this.to_player = to_player;
    }

    public DTOPlayer getWinner() {
        return winner;
    }

    public void setWinner(DTOPlayer winner) {
        this.winner = winner;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at =  new Timestamp(System.currentTimeMillis());
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }
    
    
    
}
