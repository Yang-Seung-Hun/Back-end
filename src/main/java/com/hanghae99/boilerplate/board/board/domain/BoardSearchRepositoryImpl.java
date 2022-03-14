package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.BoardSearchRepository;
import com.hanghae99.boilerplate.board.dto.BoardSearchDto;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BoardSearchRepositoryImpl implements BoardSearchRepository{
    private final ElasticsearchOperations elasticsearchOperations;
//    SELECT score() AS score, * FROM mega_market___products_product_type_1___v1
//    WHERE
//            (
//                    MATCH(name_nori, '권유리')
//    OR
//    MATCH(display_name_nori, '권유리')
//    OR
//    MATCH(content_nori, '권유리')
//  )
//    AND cate_item_id = 9
//    AND (
//            sale_price BETWEEN 100 AND 177000
//    )
//    ORDER BY score() DESC
    private static final String PRODUCT_INDEX = "boiler_plate___boards_v1";

    @Override
    public List<BoardSearchDto> searchByBoard(String name, Pageable pageable){

        // 1. Create query on multiple fields enabling fuzzy search
        QueryBuilder queryBuilder =
                QueryBuilders
                        .multiMatchQuery(name, "title_nori", "content_nori", "category_nori")
                        .fuzziness(Fuzziness.AUTO);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();


        //Query query = new CriteriaQuery(searchQuery).setPageable((org.springframework.data.domain.Pageable) pageable);
        SearchHits<BoardSearchDto> search = elasticsearchOperations.search(searchQuery, BoardSearchDto.class, IndexCoordinates.of(PRODUCT_INDEX));
        return search.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

    }

    //댓글


}
