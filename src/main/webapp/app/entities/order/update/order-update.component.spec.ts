import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderService } from '../service/order.service';
import { IOrder, Order } from '../order.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBasket } from 'app/entities/basket/basket.model';
import { BasketService } from 'app/entities/basket/service/basket.service';

import { OrderUpdateComponent } from './order-update.component';

describe('Order Management Update Component', () => {
  let comp: OrderUpdateComponent;
  let fixture: ComponentFixture<OrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderService: OrderService;
  let userService: UserService;
  let basketService: BasketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderService = TestBed.inject(OrderService);
    userService = TestBed.inject(UserService);
    basketService = TestBed.inject(BasketService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const order: IOrder = { id: 456 };
      const user: IUser = { id: 95442 };
      order.user = user;

      const userCollection: IUser[] = [{ id: 55391 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Basket query and add missing value', () => {
      const order: IOrder = { id: 456 };
      const basket: IBasket = { id: 53659 };
      order.basket = basket;

      const basketCollection: IBasket[] = [{ id: 16331 }];
      jest.spyOn(basketService, 'query').mockReturnValue(of(new HttpResponse({ body: basketCollection })));
      const additionalBaskets = [basket];
      const expectedCollection: IBasket[] = [...additionalBaskets, ...basketCollection];
      jest.spyOn(basketService, 'addBasketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(basketService.query).toHaveBeenCalled();
      expect(basketService.addBasketToCollectionIfMissing).toHaveBeenCalledWith(basketCollection, ...additionalBaskets);
      expect(comp.basketsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const order: IOrder = { id: 456 };
      const user: IUser = { id: 60405 };
      order.user = user;
      const basket: IBasket = { id: 7789 };
      order.basket = basket;

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(order));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.basketsSharedCollection).toContain(basket);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 123 };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = new Order();
      jest.spyOn(orderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(orderService.create).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 123 };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackBasketById', () => {
      it('Should return tracked Basket primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBasketById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
