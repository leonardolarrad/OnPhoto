package com.ucab.onphoto.model;

public class ReverseSearchResult {

    public final ReverseSearchObject[] match;
    public final ReverseSearchObject[] lowMatch;
    public final ReverseSearchImage[] similarImages;

    public ReverseSearchResult(ReverseSearchObject[] match,
                               ReverseSearchObject[] lowMatch,
                               ReverseSearchImage[] similarImages) {
        this.match = match;
        this.lowMatch = lowMatch;
        this.similarImages = similarImages;
    }

    public boolean hasMatches() {
        return match.length > 0;
    }

    public boolean hasLowMatches() {
        return lowMatch.length > 0;
    }

    public boolean hasSimilarImages() {
        return similarImages.length > 0;
    }

}
