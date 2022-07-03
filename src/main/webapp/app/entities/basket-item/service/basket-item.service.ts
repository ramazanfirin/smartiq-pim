import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBasketItem, getBasketItemIdentifier } from '../basket-item.model';

export type EntityResponseType = HttpResponse<IBasketItem>;
export type EntityArrayResponseType = HttpResponse<IBasketItem[]>;

@Injectable({ providedIn: 'root' })
export class BasketItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/basket-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(basketItem: IBasketItem): Observable<EntityResponseType> {
    return this.http.post<IBasketItem>(this.resourceUrl, basketItem, { observe: 'response' });
  }

  update(basketItem: IBasketItem): Observable<EntityResponseType> {
    return this.http.put<IBasketItem>(`${this.resourceUrl}/${getBasketItemIdentifier(basketItem) as number}`, basketItem, {
      observe: 'response',
    });
  }

  partialUpdate(basketItem: IBasketItem): Observable<EntityResponseType> {
    return this.http.patch<IBasketItem>(`${this.resourceUrl}/${getBasketItemIdentifier(basketItem) as number}`, basketItem, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBasketItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBasketItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBasketItemToCollectionIfMissing(
    basketItemCollection: IBasketItem[],
    ...basketItemsToCheck: (IBasketItem | null | undefined)[]
  ): IBasketItem[] {
    const basketItems: IBasketItem[] = basketItemsToCheck.filter(isPresent);
    if (basketItems.length > 0) {
      const basketItemCollectionIdentifiers = basketItemCollection.map(basketItemItem => getBasketItemIdentifier(basketItemItem)!);
      const basketItemsToAdd = basketItems.filter(basketItemItem => {
        const basketItemIdentifier = getBasketItemIdentifier(basketItemItem);
        if (basketItemIdentifier == null || basketItemCollectionIdentifiers.includes(basketItemIdentifier)) {
          return false;
        }
        basketItemCollectionIdentifiers.push(basketItemIdentifier);
        return true;
      });
      return [...basketItemsToAdd, ...basketItemCollection];
    }
    return basketItemCollection;
  }
}
