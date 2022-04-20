package com.kishlaly.ta.analyze.candles.patterns;

import com.kishlaly.ta.analyze.candles.CandlePattern;
import com.kishlaly.ta.analyze.candles.CandleResult;
import com.kishlaly.ta.model.SymbolData;

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
        return CandleResult.NO_RESULT;
    }

}
