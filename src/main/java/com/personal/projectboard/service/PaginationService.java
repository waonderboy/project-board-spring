package com.personal.projectboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNum = max(currentPageNumber - (BAR_LENGTH / 2), 0);
        int endNum = min(startNum + BAR_LENGTH, totalPages);


        return IntStream.range(startNum, endNum).boxed().toList();
    }

    public int getBarLength() {
        return BAR_LENGTH;
    }
}
