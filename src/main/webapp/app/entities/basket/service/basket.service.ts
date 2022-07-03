import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBasket, getBasketIdentifier } from '../basket.model';

export type EntityResponseType = HttpResponse<IBasket>;
export type EntityArrayResponseType = HttpResponse<IBasket[]>;

@Injectable({ providedIn: 'root' })
export class BasketService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/baskets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(basket: IBasket): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(basket);
    return this.http
      .post<IBasket>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(basket: IBasket): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(basket);
    return this.http
      .put<IBasket>(`${this.resourceUrl}/${getBasketIdentifier(basket) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(basket: IBasket): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(basket);
    return this.http
      .patch<IBasket>(`${this.resourceUrl}/${getBasketIdentifier(basket) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBasket>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBasket[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBasketToCollectionIfMissing(basketCollection: IBasket[], ...basketsToCheck: (IBasket | null | undefined)[]): IBasket[] {
    const baskets: IBasket[] = basketsToCheck.filter(isPresent);
    if (baskets.length > 0) {
      const basketCollectionIdentifiers = basketCollection.map(basketItem => getBasketIdentifier(basketItem)!);
      const basketsToAdd = baskets.filter(basketItem => {
        const basketIdentifier = getBasketIdentifier(basketItem);
        if (basketIdentifier == null || basketCollectionIdentifiers.includes(basketIdentifier)) {
          return false;
        }
        basketCollectionIdentifiers.push(basketIdentifier);
        return true;
      });
      return [...basketsToAdd, ...basketCollection];
    }
    return basketCollection;
  }

  protected convertDateFromClient(basket: IBasket): IBasket {
    return Object.assign({}, basket, {
      createDate: basket.createDate?.isValid() ? basket.createDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createDate = res.body.createDate ? dayjs(res.body.createDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((basket: IBasket) => {
        basket.createDate = basket.createDate ? dayjs(basket.createDate) : undefined;
      });
    }
    return res;
  }
}
