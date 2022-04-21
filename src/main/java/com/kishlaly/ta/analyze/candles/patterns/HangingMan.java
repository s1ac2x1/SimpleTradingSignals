package com.kishlaly.ta.analyze.candles.patterns;

import com.kishlaly.ta.analyze.candles.CandlePattern;
import com.kishlaly.ta.analyze.candles.CandleResult;
import com.kishlaly.ta.model.SymbolData;
import com.kishlaly.ta.model.indicators.Indicator;
import com.kishlaly.ta.utils.IndicatorUtils;

/**
 * Ситуация: бычий тренд становится восприимчивым к новым продавцам
 * Базовые признаки:
 * + ЕМА растет
 * + Цвет не имеет значения
 * + Тело находится в верхней части ценового диапазона
 * + Нижняя тень в два раза длиннее тела
 * + Верхняя тень отсутствует или очень короткая
 * Дополняющие признаки:
 * + Следующая котировка красная (необязательно)
 * + Следующая котировка открылась ниже тела повешенного (необязательно, но хороший признак)
 */
public class HangingMan implements CandlePattern {

    @Override
    public CandleResult check(SymbolData screen) {
        // ЕМА растет
        if (IndicatorUtils.emaAscending(screen.indicators.get(Indicator.EMA13), 3, 4)) {
            // Тело находится в верхней части ценового диапазона
        }

        return CandleResult.NO_RESULT;
    }

}
