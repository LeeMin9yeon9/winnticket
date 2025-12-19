package kr.co.winnticket.common.typehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.co.winnticket.product.admin.dto.StayPeriodDto;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.List;

public class JsonStayPeriodListTypeHandler
        extends BaseTypeHandler<List<StayPeriodDto>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            List<StayPeriodDto> parameter,
            JdbcType jdbcType
    ) throws SQLException {
        try {
            ps.setObject(i, objectMapper.writeValueAsString(parameter), Types.OTHER);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<StayPeriodDto> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return read(rs.getString(columnName));
    }

    @Override
    public List<StayPeriodDto> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return read(rs.getString(columnIndex));
    }

    @Override
    public List<StayPeriodDto> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return read(cs.getString(columnIndex));
    }

    private List<StayPeriodDto> read(String json) throws SQLException {
        if (json == null) return List.of();

        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<StayPeriodDto>>() {}
            );
        } catch (Exception e) {
            throw new SQLException("Failed to convert JSON to List<StayPeriodDto>", e);
        }
    }
}
