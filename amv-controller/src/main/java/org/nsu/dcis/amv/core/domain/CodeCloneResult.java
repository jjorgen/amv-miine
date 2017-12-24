package org.nsu.dcis.amv.core.domain;

import com.github.javaparser.ast.MethodRepresentation;
import org.apache.log4j.Logger;
import org.nsu.dcis.amv.core.util.Pair;

import static org.nsu.dcis.amv.core.domain.CodeCloneResult.Type.*;

/**
 * Created by John Jorgensen on 3/7/2017.
 */
public class CodeCloneResult {
    public static final int TOP_OF_METHOD_TOKEN_THRESHOLD = 4;
    public static final int BOTTOM_OF_METHOD_TOKEN_THRESHOLD = 4;

    private int fromTopOfMethodLineCount;
    private int fromBottomMethodLineCount;
    private Logger log = Logger.getLogger(getClass().getName());

    private Pair<MethodRepresentation, MethodRepresentation> methodPair;

    public CodeCloneResult() {
    }

    public boolean isSameMethods() {
        return isEmpty();
    }

    public enum Type {
        BEFORE_ADVICE_CANDIDATE, AFTER_ADVICE_CANDIDATE, AROUND_ADVICE_CANDIDATE, CLONE, EMPTY;
    };

    public Type getType() {
        if (isEmpty()) {
            return EMPTY;
        } else if (isClone()) {
            return CLONE;
        } else if (isAroundAdviceCandidate()) {
            return AROUND_ADVICE_CANDIDATE;
        } else if (isBeforeAdviceCandidate()) {
            return BEFORE_ADVICE_CANDIDATE;
        } else if (isAfterAdviceCandidate()) {
            return AFTER_ADVICE_CANDIDATE;
        } else {
            throw new IllegalStateException("Invalid code clone mining result");
        }
    }

    public CodeCloneResult(MethodRepresentation compareFrom, MethodRepresentation compareTo, int fromTopOfMethodLineCount, int fromBottomMethodLineCount) {
        this.fromTopOfMethodLineCount = fromTopOfMethodLineCount;
        this.fromBottomMethodLineCount = fromBottomMethodLineCount;
        methodPair = new Pair<MethodRepresentation, MethodRepresentation>(compareFrom, compareTo);
    }

    public boolean isClone() {
        if (isEmpty()) return false;
        int firsMethodSize = ((MethodRepresentation)methodPair.getFirst()).getMethodTokens().size();
        int secondMethodSize = ((MethodRepresentation)methodPair.getSecond()).getMethodTokens().size();

        if (fromTopOfMethodLineCount == fromBottomMethodLineCount && firsMethodSize == secondMethodSize &&
            fromTopOfMethodLineCount == firsMethodSize && fromBottomMethodLineCount == firsMethodSize) {
            return true;
        }
        return false;
    }

    public boolean isAroundAdviceCandidate() {
        if (isEmpty()) return false;
        if (fromTopOfMethodLineCount > TOP_OF_METHOD_TOKEN_THRESHOLD &&
            fromBottomMethodLineCount > BOTTOM_OF_METHOD_TOKEN_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAfterAdviceCandidate() {
        if (isEmpty()) return false;
        if (fromBottomMethodLineCount > BOTTOM_OF_METHOD_TOKEN_THRESHOLD && fromTopOfMethodLineCount <= TOP_OF_METHOD_TOKEN_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBeforeAdviceCandidate() {
        if (isEmpty()) return false;
        if (fromTopOfMethodLineCount > TOP_OF_METHOD_TOKEN_THRESHOLD && fromBottomMethodLineCount <= BOTTOM_OF_METHOD_TOKEN_THRESHOLD) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        if (methodPair == null) return true;
        return false;
    }

    public Pair<MethodRepresentation, MethodRepresentation> getMethodPair() {
        return methodPair;
    }

    public void display() {
        MethodRepresentation compareFrom = getMethodPair().getFirst();
        MethodRepresentation compareTo = getMethodPair().getSecond();
        log.info("\nAdvice Candidate Type: " + getType() +
        "\nFirst Method: " + compareFrom.getFullMethodName() +
        "\n" + compareFrom.getStringifiedWithoutComments() +
        "\nSecond Method: " + compareTo.getFullMethodName() +
        "\n" + compareTo.getStringifiedWithoutComments());
    }

    public void displaytemp() {
        log.info("\nAdvice Candidate Type: " + getType());
        MethodRepresentation compareFrom = getMethodPair().getFirst();
        log.info("\nFrom Method Name: " + compareFrom.getFullMethodName());
        log.info("\nClone: compareFrom: " + compareFrom.getStringifiedWithoutComments());
        MethodRepresentation compareTo = getMethodPair().getSecond();
        log.info("\nTo Method Name:   " + compareTo.getFullMethodName());
        log.info("\nClone: compareTo: " + compareTo.getStringifiedWithoutComments());
    }
}